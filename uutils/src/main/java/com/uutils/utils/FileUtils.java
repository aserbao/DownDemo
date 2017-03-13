package com.uutils.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtils {
    /***
     * 防止路径符有问题
     *
     * @param file
     * @return
     */
    public static File getFileSafe(String file) {
        File f = null;
        try {
            f = new File(file);
        } catch (Exception e) {
            Logs.e(e);
        }
        return f;
    }

    /***
     * 创建目录
     *
     * @param dirname
     * @return
     */
    public static boolean createDir(String dirname) {
        if (dirname == null || "/".equals(dirname)) return false;
        File localFile = getFileSafe(dirname);
        if (localFile == null || localFile.isFile()) return false;
        if (!localFile.exists()) return localFile.mkdirs();
        return true;
    }

    public static String getDownloadFileType(String url) {
        String name = getFileType(url);
        if (name != null) {
            if (".php".equals(name) || ".asp".equals(name) || ".jsp".equals(name) || ".html".equals(name) || ".htm".equals(name)) {
                name = "";
            }
        }
        return name;
    }

    public static String getFileType(String url) {
        String name = getFileName(url);
        if (name != null) {
            int h = name.lastIndexOf('?');
            if (h > 1) {
                name = name.substring(0, h);
            }
            int i = name.lastIndexOf(".");
            if (i > 0) {
                name = name.substring(i).replace("*", "").replace(":", "").toLowerCase(Locale.US);
            } else {
                name = "";
            }
        }
        return name;
    }

    public static String getFileName(String file) {
        if (file == null) return "";
        if (!file.startsWith("http")) {
            File localFile = getFileSafe(file);
            if (localFile != null) {
                return localFile.getName();
            }
        }
        if (file.endsWith("/")) file = file.substring(0, file.length() - 1);
        int i = file.lastIndexOf("/");
        return (i >= 0) ? file.substring(i + 1) : file;
    }

    /***
     * 获取文件大小
     *
     * @param url
     * @return
     */
    public static long getFileSize(String url) {
        File file = getFileSafe(url);
        if (file == null) {
            return 0;
        }
        return file.length();

    }

    /**
     * 通过文件获取文件夹名字
     *
     * @param file
     * @return
     */
    public static String getDirByFile(String file) {
        if (file == null) return "";
        File localFile = getFileSafe(file);
        if (localFile != null) {
            return localFile.getParent();
        }
        int i = file.lastIndexOf("/");
        return (i > 0) ? file.substring(0, i + 1) : "/";
    }

    /***
     * 获取sd卡
     *
     * @return
     */
    public static String getStoragePath() {
        try {
            String str = Environment.getExternalStorageDirectory().getAbsolutePath();
            return str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/storage/emulated/legacy/";
    }

    /***
     * 获取sd卡自由空间
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static long getSDFreeSize() {
        File path = Environment.getExternalStorageDirectory(); // 取得SD卡文件路径
        StatFs stat = new StatFs(path.getPath());
        if (Build.VERSION.SDK_INT < 19) {
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return blockSize * availableBlocks;
        } else {
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            return blockSize * availableBlocks;
        }
    }

    /***
     * 组合路径
     *
     * @param paths
     * @return
     */
    public static String combine(String... paths) {
        if (paths == null || paths.length == 0) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder();
            String spliter = File.separator;
            String firstPath = paths[0];
            if ("http".equals(firstPath.toLowerCase(Locale.US))) {
                spliter = "/";
            }
            if (!firstPath.endsWith(spliter)) {
                firstPath = firstPath + spliter;
            }
            builder.append(firstPath);
            for (int i = 1; i < paths.length; i++) {
                String nextPath = paths[i];
                if(nextPath!=null) {
                    if (nextPath.startsWith("/") || nextPath.startsWith("\\")) {
                        nextPath = nextPath.substring(1);
                    }
                    if (i != paths.length - 1) // not the last one
                    {
                        if (nextPath.endsWith("/") || nextPath.endsWith("\\")) {
                            nextPath = nextPath.substring(0, nextPath.length() - 1) + spliter;
                        } else {
                            nextPath = nextPath + spliter;
                        }
                    }
                    builder.append(nextPath);
                }else {
                    builder.append("null");
                }

            }
            return builder.toString();
        }
    }

    /***
     * 获取sd卡路径
     *
     * @param file
     * @return
     */
    public static String getSDCardPath(String file) {
        return combine(getStoragePath(), file);
    }

    /***
     * 重命名
     *
     * @return
     */
    public static boolean renameFile(String form, String dest) {
        File fromfile = getFileSafe(form);
        if (fromfile != null && fromfile.exists()) {
            File destfile = getFileSafe(dest);
            if (destfile != null) {
                if (destfile.exists()) {
                    destfile.delete();
                }
                return fromfile.renameTo(destfile);
            }
        }
        return false;
    }

    /***
     * 改临时文件名
     *
     * @param from
     * @return
     */
    public static boolean renameTmpFile(String from) {
        if (from == null) return false;
        if (from.endsWith(".tmp")) {
            String to = from.substring(0, from.length() - 4);
            return renameFile(from, to);
        }
        return renameFile(from + ".tmp", from);
    }

    /**
     * 返回的路径为/data/data/包/name
     */
    public static String getFilesByName(Context context, String name) {
        return new File(context.getFilesDir().getAbsolutePath(), name).getAbsolutePath();
    }

    public static boolean delete(String f) {
        File file = getFileSafe(f);
        return delete(file);
    }

    /***
     * 删除文件或者文件夹
     *
     * @param f
     * @return
     */
    public static boolean delete(File f) {
        if (f == null || !f.exists()) {
            return true;
        }
        try {
            if (f.isDirectory()) {
                if (f.listFiles().length == 0) {
                    f.delete();
                } else {
                    File delFile[] = f.listFiles();
                    int i = f.listFiles().length;
                    for (int j = 0; j < i; j++) {
                        delete(delFile[j]);
                    }
                    f.delete();
                }
            } else {
                f.delete();
            }
            return true;
        } catch (Exception e) {

        }
        return false;
    }

    /***
     * 文件存在
     *
     * @param path
     * @return
     */
    public static boolean exists(String path) {
        File file = getFileSafe(path);
        return file != null && file.exists();
    }

    /***
     * 读取文件
     *
     * @param file
     * @return
     */
    public static byte[] readFile(String file) {
        byte[] data = null;
        if (file == null || !exists(file)) return data;
        InputStream inputstream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            inputstream = new FileInputStream(file);
            outputStream = new ByteArrayOutputStream();
            write(inputstream, outputStream);
            data = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(outputStream);
            close(inputstream);
        }

        return data;
    }

    /***
     * 写入到文件
     *
     * @param file
     * @param data
     * @return
     */
    public static boolean writeFile(String file, byte[] data) {
        if (file == null) return false;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        boolean ok = false;
        try {
            File f = new File(file);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            inputStream = new ByteArrayInputStream(data);
            outputStream = new FileOutputStream(file);
            write(inputStream, outputStream);
            ok = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(outputStream);
            close(inputStream);
        }
        return ok;
    }

    /***
     * 复制文件
     *
     * @param file
     * @param toFile
     * @return
     */
    public static boolean copyFile(String file, String toFile) {
        boolean isOK = false;
        if (file == null || toFile == null || !createDir(getDirByFile(toFile))) return isOK;
        InputStream inputstream = null;
        OutputStream outputStream = null;
        try {
            inputstream = new FileInputStream(file);
            outputStream = new FileOutputStream(toFile);
            isOK = write(inputstream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            isOK = false;
        } finally {
            close(outputStream);
            close(inputstream);
        }
        return isOK;
    }

    /***
     * 从assets复制文件
     *
     * @param context
     * @param FileName
     * @param destName
     * @return
     */
    public static boolean copyFromAsset(Context context, String FileName, String destName) {
        boolean isOK = false;
        if (FileName == null || destName == null || !createDir(getDirByFile(destName))) return isOK;
        InputStream inputstream = null;
        OutputStream outputStream = null;
        try {
            inputstream = context.getAssets().open(FileName);
            outputStream = new FileOutputStream(destName);
            isOK = write(inputstream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            isOK = false;
        } finally {
            close(outputStream);
            close(inputstream);
        }
        return isOK;
    }

    /***
     * 从zip复制文件
     *
     * @param zipFileName
     * @param zipEntryName
     * @param destName
     * @return
     */
    public static boolean copyFromZip(String zipFileName, String zipEntryName, String destName) {
        File zip = new File(zipFileName);
        if (!zip.exists()) return false;
        if (destName == null) return false;
        //创建文件夹
        createDir(getDirByFile(destName));
        boolean isOK = false;
        ZipFile zf = null;
        InputStream is = null;
        OutputStream outputStream = null;
        try {
            zf = new ZipFile(zip);
            ZipEntry ZEpng = zf.getEntry(zipEntryName);
            if (ZEpng != null) {
                is = zf.getInputStream(ZEpng);
                outputStream = new FileOutputStream(destName);
                isOK = write(is, outputStream);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(is);
            close(zf);
            close(outputStream);
        }
        return isOK;
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean write(InputStream inputStream, OutputStream outputStream) throws IOException {
        if (inputStream == null || outputStream == null) {
            return false;
        }
        byte[] readBytes = new byte[4096];
        int i = -1;
        while ((i = inputStream.read(readBytes)) > 0) {
            outputStream.write(readBytes, 0, i);
        }
        return true;
    }

    public static File[] listFilesByDir(File dirFile) {
//        String dir = FileUtils.getDirByFile(filepath);
//        File dirFile = new File(dir);
        if (dirFile.exists() && dirFile.isDirectory()) {
            File[] fileArray = dirFile.listFiles();
            if (fileArray != null && fileArray.length > 0) {
                return fileArray;
            }
        }
        return null;
    }

    public static String getFromAssets(String fileName, Context context) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null) Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
