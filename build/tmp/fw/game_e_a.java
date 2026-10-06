package com.corrodinggames.rts.gameFramework.e;

public class FileLoader {
	public static final FileLoaderBackend defaultBackend = new FileLoaderBackend();
	public static FileLoaderBackend backend = defaultBackend;
	public static Boolean externalStoragePermission;
	public static String overriddenExternalPath;
	public static String loadError;

	protected static String getInternalAppPath() {
		Context var0 = AppContext.getApplicationContext();
		File var1 = var0.b(null);
		if (var1 != null) {
			return var1.getAbsolutePath();
		} else {
			GameEngine.logWarning("Failed to get an internal path.");
			return null;
		}
	}

	public static void initBackends() {
		loadError = null;
		if (GameEngine.isNotDedicatedServer()) {
			if (VERSION.SDK_INT < 19) {
				loadError = "Android version too old for new file system support";
				GameEngine.log("FileLoader: SDK too old, not changing FileLoader");
				return;
			}

			int var0 = GameEngine.getInstance().settings.storageType;
			GameEngine.log("FileLoader: storageBehaviour:" + var0);
			FileLoaderBackend var1 = createBackendForStorageType(var0);
			GameEngine.log("Using file loader: " + var1.getDisplayName());
			backend = var1;
		}
	}

	public static boolean isMountedPath(String string) {
		return backend.isMountedPath(string);
	}

	public static FileAccessConfig computeFileAccessConfig(boolean boolean1) {
		FileAccessConfig var1 = new FileAccessConfig();
		if (!GameEngine.isNotDedicatedServer()) {
			var1.useSafExternalAccess = false;
			var1.useDirectExternalAccess = true;
			return var1;
		} else if (VERSION.SDK_INT < 19) {
			var1.useSafExternalAccess = false;
			var1.useDirectExternalAccess = true;
			return var1;
		} else {
			var1.useSafExternalAccess = true;
			var1.useOverriddenExternalPath = false;
			if (overriddenExternalPath != null) {
				var1.useOverriddenExternalPath = true;
			}

			if (externalStoragePermission != null && !externalStoragePermission) {
				var1.useDirectExternalAccess = true;
				var1.useSafExternalAccess = false;
				var1.useOverriddenExternalPath = false;
			}

			if (VERSION.SDK_INT <= 28 && externalStoragePermission == null) {
				GameEngine.logWarning("FileLoader using direct external access due to sdk: " + VERSION.SDK_INT);
				var1.useDirectExternalAccess = true;
				var1.useSafExternalAccess = false;
				var1.useOverriddenExternalPath = false;
			}

			return var1;
		}
	}

	public static FileLoaderBackend createBackendForStorageType(int integer) {
		if (!GameEngine.isNotDedicatedServer()) {
			return new FileLoaderBackend();
		} else if (VERSION.SDK_INT >= 19) {
			String var1 = getInternalAppPath();
			DirectoryBackend var3 = null;
			if (var1 == null) {
				loadError = "Failed to get internal app path (is it unmounted?).";
				integer = 3;
			} else {
				var3 = new DirectoryBackend(var1, "internal");
				var3.displayPrefix = "Internal: ";
			}

			FileAccessConfig var4 = computeFileAccessConfig(false);
			Object var5;
			if (!var4.useOverriddenExternalPath) {
				if (!var4.useDirectExternalAccess) {
					GameEngine.logWarning("Not using direct external backend: As direct reads will cause problems");
					var5 = null;
					integer = 0;
				} else {
					GameEngine.logWarning("FileLoader using direct external file access! SDK:" + VERSION.SDK_INT);
					var5 = new FileLoaderBackend();
				}
			} else {
				GameEngine.log("FileLoader using overriddenExternalPath:" + overriddenExternalPath);
				var5 = new DirectoryBackend(overriddenExternalPath, "external");
			}

			NullBackend var6 = new NullBackend();
			if (integer != 3 && var3 == null) {
				GameEngine.logWarning("No available file backends!!");
				return var6;
			} else {
				DualPathBackend var2;
				if (integer == 1) {
					var2 = new DualPathBackend(var3, "[INTERNAL-PATH]/", (FileLoaderBackend)var5, "[EXTERNAL-PATH]/");
				} else if (integer == 2) {
					var2 = new DualPathBackend((FileLoaderBackend)var5, "[EXTERNAL-PATH]/", var3, "[INTERNAL-PATH]/");
				} else if (integer == 3) {
					var2 = new DualPathBackend((FileLoaderBackend)var5, "[EXTERNAL-PATH]/", var6, "[NULL-PATH]/");
				} else {
					var2 = new DualPathBackend(var3, "[INTERNAL-PATH]/", var6, "[NULL-PATH]/");
				}

				var2.secondaryBackend.disableAssets = true;
				return var2;
			}
		} else {
			GameEngine.log("FileLoader: SDK too old, not changing FileLoader");
			return new FileLoaderBackend();
		}
	}

	public static String takeErrorMessage() {
		return backend.takeErrorMessage();
	}

	public static void setErrorMessage(String string) {
		backend.setErrorMessage(string);
	}

	public static String findFileWithAnyExtension(String string1, String string2) {
		return backend.findFileWithAnyExtension(string1, string2);
	}

	public static boolean isAssetPath(String string) {
		return backend.isAssetPath(string);
	}

	public static String convertAbstractPathForDisplay(String string) {
		return backend.convertAbstractPathForDisplay(string);
	}

	public static String convertAbstractPath(String string) {
		return backend.convertAbstractPath(string);
	}

	public static boolean isDirectory(String string) {
		return backend.isDirectory(string, false);
	}

	public static boolean isDirectoryPreferMount(String string) {
		return backend.isDirectory(string, true);
	}

	public static String[] listDir(String string) {
		return backend.listDir(string, false);
	}

	public static String[] listDir(String string, boolean boolean2) {
		return backend.listDir(string, boolean2);
	}

	public static boolean fileExists(String string) {
		return backend.fileExists(string);
	}

	public static AssetInputStream openAsset(String string) {
		return backend.openAsset(string);
	}

	public static AssetInputStream openByPath(File file) {
		return backend.openByAbstractPath(file.getAbsolutePath());
	}

	public static AssetInputStream openByPath(String string) {
		return backend.openByAbstractPath(string);
	}

	public static OutputStream openOutput(File file, boolean boolean2) {
		return backend.openOutput(file.getAbsolutePath(), boolean2);
	}

	public static OutputStream openOutput(String string, boolean boolean2) {
		return backend.openOutput(string, boolean2);
	}

	public static boolean createDirectory(String string) {
		return backend.createDirectory(string);
	}

	public static String getStorageRootPath() {
		return backend.getStorageRootPath();
	}

	public static String getCachePath() {
		return backend.getCachePath();
	}

	public static long lastModified(String string) {
		return backend.lastModified(string);
	}

	public static File getRWFile(String string1, String string2, boolean boolean3) {
		return backend.getRWFile(string1, string2, boolean3);
	}

	public static boolean renameFileDirect(File file1, File file2) {
		if (GameEngine.isDesktopVersion() && file2.exists()) {
			file2.delete();
		}

		return file1.renameTo(file2);
	}

	public static boolean renameFile(File file1, File file2) {
		return backend.renameFile(file1, file2);
	}

	public static boolean deleteFile(File file) {
		return backend.deleteFile(file);
	}

	public static String stripVirtualPathTags(String string) {
		return backend.getBackendName(string);
	}

	public static boolean f() {
		return backend.e();
	}

	public static String o(String string) {
		return backend.stripVirtualPathTags(string);
	}

	public static String getBackendName(String string) {
		return backend.addPrimaryPathTag(string);
	}

	public static File createTempFile(Context context, String string2, String string3) {
		try {
			File var3 = context.i();
			return File.createTempFile(string2, string3, var3);
		} catch (IOException var7) {
			try {
				File var4 = context.j();
				return File.createTempFile(string2, string3, var4);
			} catch (IOException var6) {
				var7.printStackTrace();
				throw var6;
			}
		}
	}

	public static void c(File file) {
		backend.a(file);
	}
}
