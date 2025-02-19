use cxx_build;
use std::env;
use std::path::{Path, PathBuf};
use std::process::Command;
use std::error::Error;

fn build_taglib(target: &str, working_dir: &Path) -> Result<PathBuf, Box<dyn Error>> {
    let taglib_src_dir = working_dir.join("taglib");
    let taglib_build_dir = taglib_src_dir.join("build");
    let taglib_pkg_dir = taglib_src_dir.join("pkg");
    let arch_build_dir = taglib_build_dir.join(&target);
    let arch_pkg_dir = taglib_pkg_dir.join(&target);

    // If lib/libtag.a exists, we don't need to build it
    if arch_pkg_dir.join("lib/libtag.a").exists() {
        return Ok(arch_pkg_dir);
    }

    // Prepare basic cmake arguments
    let mut cmake_args = vec![
        "-B".to_string(),
        arch_build_dir.to_str().unwrap().to_string(),
        "-DBUILD_SHARED_LIBS=OFF".to_string(),
        "-DVISIBILITY_HIDDEN=ON".to_string(),
        "-DBUILD_TESTING=OFF".to_string(),
        "-DBUILD_EXAMPLES=OFF".to_string(),
        "-DBUILD_BINDINGS=OFF".to_string(),
        "-DWITH_ZLIB=OFF".to_string(),
        "-DCMAKE_BUILD_TYPE=Release".to_string(),
        "-DCMAKE_CXX_FLAGS=-fPIC".to_string(),
    ];

    if target.contains("android") {
        // Android target, we need to tack on the NDK toolchain file/args
        let arch = if target == "x86_64-linux-android" {
            "x86_64"
        } else if target.contains("i686-linux-android") {
            "x86"
        } else if target.contains("aarch64-linux-android") {
            "arm64-v8a"
        } else if target.contains("armv7-linux-androideabi") {
            "armeabi-v7a"
        } else {
            // should never happen
            return Err(format!("Unsupported Android target: {}", target).into());
        };

        let clang_path = env::var("CLANG_PATH").expect("CLANG_PATH env var not set");
        let toolchains_marker = "/toolchains";
        let ndk_path = if let Some(pos) = clang_path.find(toolchains_marker) {
            &clang_path[..pos]
        } else {
            return Err(format!("CLANG_PATH does not contain '{}'", toolchains_marker).into());
        };

        let ndk_toolchain = working_dir.join("android.toolchain.cmake");

        cmake_args.extend(vec![
            format!("-DANDROID_NDK_PATH={}", ndk_path),
            format!("-DCMAKE_TOOLCHAIN_FILE={}", ndk_toolchain.to_str().unwrap()),
            format!("-DANDROID_ABI={}", arch),
        ]);
    }

    // Configure step
    let status = Command::new("cmake")
        .args(&cmake_args)
        .current_dir(&taglib_src_dir)
        .status()?;
    if !status.success() {
        return Err(format!("cmake configure failed for target {}", target).into());
    }

    // Build step
    let status = Command::new("cmake")
        .arg("--build")
        .arg(arch_build_dir.to_str().unwrap())
        .arg("--config")
        .arg("Release")
        .arg(format!(
            "-j{}",
            std::thread::available_parallelism().unwrap().get()
        ))
        .status()?;
    if !status.success() {
        return Err(format!("cmake build failed for target {}", target).into());
    }

    // Install step
    let status = Command::new("cmake")
        .arg("--install")
        .arg(arch_build_dir.to_str().unwrap())
        .arg("--config")
        .arg("Release")
        .arg("--prefix")
        .arg(arch_pkg_dir.to_str().unwrap())
        .arg("--strip")
        .status()?;
    if !status.success() {
        return Err(format!("cmake install failed for arch {}", target).into());
    }

    Ok(arch_pkg_dir)
}

fn configure_cxx_bridge(target: &str) -> Result<(), Box<dyn Error>> {
    let mut builder = cxx_build::bridge("src/taglib/bridge.rs");
    builder
        .file("shim/iostream_shim.cpp")
        .file("shim/file_shim.cpp")
        .file("shim/tk_shim.cpp")
        .file("shim/picture_shim.cpp")
        .file("shim/xiph_shim.cpp")
        .file("shim/id3v1_shim.cpp")
        .file("shim/id3v2_shim.cpp")
        .file("shim/mp4_shim.cpp")
        .include(format!("taglib/pkg/{}/include", target))
        .include(".")
        .flag_if_supported("-std=c++14");

    if target.contains("android") {
        builder
            .cpp_link_stdlib("c++_static")
            // Magic linker flags that statically link exception handling symbols
            // to the library.
            .flag("-fexceptions")
            .flag("-funwind-tables");
    }
    builder.compile("taglib_cxx_bindings");

    Ok(())
}

fn main() -> Result<(), Box<dyn Error>> {
    let working_dir = env::current_dir()?;
    let target = env::var("TARGET")?;
    let working_dir = Path::new(&working_dir);

    let arch_pkg_dir = build_taglib(&target, &working_dir)?;
    println!(
        "cargo:rustc-link-search=native={}/lib",
        arch_pkg_dir.display()
    );
    println!("cargo:rustc-link-lib=static=tag");
    println!("cargo:rerun-if-changed=taglib/");

    configure_cxx_bridge(&target)?;
    if target.contains("android") {
        // Magic linker flags that statically link the C++ runtime
        // and exception handling to the library.
        println!("cargo:rustc-link-lib=static=c++_static");
        println!("cargo:rustc-link-lib=static=c++abi");
        println!("cargo:rustc-link-lib=unwind");
    }
    println!("cargo:rerun-if-changed=shim/");

    Ok(())
}
