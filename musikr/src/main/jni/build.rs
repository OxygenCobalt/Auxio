use std::env;
use std::path::Path;
use std::process::Command;
use cxx_build;

fn main() {
    let working_dir = env::current_dir().expect("Failed to get current working directory");

    // New: Extract ndk path from CLANG_PATH.
    let clang_path = env::var("CLANG_PATH").expect("CLANG_PATH env var not set");
    let toolchains_marker = "/toolchains";
    let ndk_path = if let Some(pos) = clang_path.find(toolchains_marker) {
        &clang_path[..pos]
    } else {
        panic!("CLANG_PATH does not contain '{}'", toolchains_marker);
    };

    let working_dir = Path::new(&working_dir);

    // Define directories.
    let taglib_src_dir = working_dir.join("taglib");
    let taglib_build_dir = taglib_src_dir.join("build");
    let taglib_pkg_dir = taglib_src_dir.join("pkg");
    let ndk_toolchain = working_dir.join("android.toolchain.cmake");

    // Define architectures.
    let target = env::var("TARGET").expect("TARGET env var not set");
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

    // Configure step.
    let status = Command::new("cmake")
        .arg("-B")
        .arg(arch_build_dir.to_str().unwrap())
        .arg(format!("-DANDROID_NDK_PATH={}", ndk_path))
        .arg(format!(
            "-DCMAKE_TOOLCHAIN_FILE={}",
            ndk_toolchain.to_str().unwrap()
        ))
        .arg(format!("-DANDROID_ABI={}", arch))
        .arg("-DBUILD_SHARED_LIBS=OFF")
        .arg("-DVISIBILITY_HIDDEN=ON")
        .arg("-DBUILD_TESTING=OFF")
        .arg("-DBUILD_EXAMPLES=OFF")
        .arg("-DBUILD_BINDINGS=OFF")
        .arg("-DWITH_ZLIB=OFF")
        .arg("-DCMAKE_BUILD_TYPE=Release")
        .arg("-DCMAKE_CXX_FLAGS=-fPIC")
        .current_dir(&taglib_src_dir)
        .status()
        .expect("Failed to run cmake configure");
    if !status.success() {
        panic!("cmake configure failed for arch {}", arch);
    }

    // Build step.
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

    // Install step.
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

    cxx_build::bridge("src/lib.rs")
        .include(format!["taglib/pkg/{}/include", arch]) // path to Taglib includes
        .flag_if_supported("-std=c++14")
        .compile("taglib_cxx_bindings");
    // println!("cargo:rerun-if-changed=src/lib.rs");
}
