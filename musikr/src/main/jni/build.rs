use std::env;
use std::path::Path;
use std::process::Command;
use cxx_build;

fn main() {
    let working_dir = env::current_dir().expect("Failed to get current working directory");
    let target = env::var("TARGET").expect("TARGET env var not set");
    let working_dir = Path::new(&working_dir);

    // Define directories
    let taglib_src_dir = working_dir.join("taglib");
    let taglib_build_dir = taglib_src_dir.join("build");
    let taglib_pkg_dir = taglib_src_dir.join("pkg");

    // Determine if building for Android
    let is_android = target.contains("android");

    // Get architecture
    let arch = if target.contains("x86_64") {
        "x86_64"
    } else if target.contains("i686") {
        "x86"
    } else if target.contains("aarch64") {
        "arm64-v8a"
    } else if target.contains("arm") {
        "armeabi-v7a"
    } else {
        "unknown"
    };

    let arch_build_dir = taglib_build_dir.join(arch);
    let arch_pkg_dir = taglib_pkg_dir.join(arch);

    // Prepare cmake command
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

    // Add Android-specific arguments if building for Android
    if is_android {
        let clang_path = env::var("CLANG_PATH").expect("CLANG_PATH env var not set");
        let toolchains_marker = "/toolchains";
        let ndk_path = if let Some(pos) = clang_path.find(toolchains_marker) {
            &clang_path[..pos]
        } else {
            panic!("CLANG_PATH does not contain '{}'", toolchains_marker);
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
        .status()
        .expect("Failed to run cmake configure");
    if !status.success() {
        panic!("cmake configure failed for arch {}", arch);
    }

    // Build step
    let status = Command::new("cmake")
        .arg("--build")
        .arg(arch_build_dir.to_str().unwrap())
        .arg("--config")
        .arg("Release")
        .arg(format!("-j{}", std::thread::available_parallelism().unwrap().get()))
        .status()
        .expect("Failed to run cmake build");
    if !status.success() {
        panic!("cmake build failed for arch {}", arch);
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
        .status()
        .expect("Failed to run cmake install");
    if !status.success() {
        panic!("cmake install failed for arch {}", arch);
    }

    println!("cargo:rustc-link-search=native={}/lib", arch_pkg_dir.display());
    println!("cargo:rustc-link-lib=static=tag");
    
    // Build the shim and cxx bridge together
    cxx_build::bridge("src/taglib/ffi.rs")
        .file("shim/iostream_shim.cpp")
        .file("shim/file_shim.cpp")
        .file("shim/tk_shim.cpp")
        .include(format!("taglib/pkg/{}/include", arch))
        .include("shim")
        .include(".")  // Add the current directory to include path
        .flag_if_supported("-std=c++14")
        .cpp_link_stdlib("c++_shared")  // Use shared C++ runtime for Android compatibility
        .compile("taglib_cxx_bindings");

    // Rebuild if shim files change
    println!("cargo:rerun-if-changed=shim/iostream_shim.hpp");
    println!("cargo:rerun-if-changed=shim/iostream_shim.cpp");
    println!("cargo:rerun-if-changed=shim/file_shim.hpp");
    println!("cargo:rerun-if-changed=shim/file_shim.cpp");
    println!("cargo:rerun-if-changed=shim/tk_shim.hpp");
    println!("cargo:rerun-if-changed=shim/tk_shim.cpp");
    println!("cargo:rerun-if-changed=src/taglib/ffi.rs");
}
