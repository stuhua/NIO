package github.stuhua.nio.utils;

import android.content.Intent;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.ant.types.FileSet;


import java.io.*;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by liulh on 2017/5/26 17:27 星期五
 */

public class FileUtil {
    /**
     * 创建目录
     *
     * @param dir 欲创建目录路径
     * @return 创建成功返回true，目录已存在或创建失败返回false
     */
    public static boolean createDirectory(String dir) {
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
            return true;
        }
        return false;
    }

    /**
     * 创建文件
     *
     * @param fileDirectoryAndName 路径
     * @param fileContent          内容
     */
    public static void createNewFile(String fileDirectoryAndName, String fileContent) {
        try {
            //创建File对象，参数为String类型，表示目录名
            File myFile = new File(fileDirectoryAndName);
            //判断文件是否存在，如果不存在则调用createNewFile()方法创建新目录，否则跳至异常处理代码
            if (!myFile.exists())
                myFile.createNewFile();
            else  //如果不存在则扔出异常
                throw new Exception("The new file already exists!");
            //下面把数据写入创建的文件
            write(fileContent, fileDirectoryAndName);
        } catch (Exception ex) {
            System.out.println("无法创建新文件！");
            ex.printStackTrace();
        }
    }

    /**
     * 保存信息到指定文件
     *
     * @param physicalPath 保存文件物理路径
     * @param inputStream  目标文件的输入流
     * @return 保存成功返回true，反之返回false
     */
    public static boolean saveFileByPhysicalDir(String physicalPath, InputStream inputStream) {
        boolean flag = false;
        try {
            OutputStream os = new FileOutputStream(physicalPath);
            int readBytes = 0;
            byte buffer[] = new byte[8192];
            while ((readBytes = inputStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, readBytes);
            }
            os.close();
            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 保存字符串到指定路径
     *
     * @param physicalPath 保存物理路径
     * @param content      欲保存的字符串
     */
    public static void saveAsFileOutputStream(String physicalPath, String content) {
        File file = new File(physicalPath);
        boolean b = file.getParentFile().isDirectory();
        if (!b) {
            File tem = new File(file.getParent());
            tem.mkdirs();// 创建目录
        }
        FileOutputStream foutput = null;
        try {
            foutput = new FileOutputStream(physicalPath);
            foutput.write(content.getBytes("UTF-8"));
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        } finally {
            try {
                foutput.flush();
                foutput.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * 向文件添加信息（不会覆盖原文件内容）
     *
     * @param tivoliMsg   要写入的信息
     * @param logFileName 目标文件
     */
    public static void write(String tivoliMsg, String logFileName) {
        try {
            byte[] bMsg = tivoliMsg.getBytes("UTF-8");
            FileOutputStream fOut = new FileOutputStream(logFileName, true);
            fOut.write(bMsg);
            fOut.close();
        } catch (IOException e) {
        }
    }

    /**
     * 日志写入
     * 例如：
     * 2016/01/08 17:46:42 : 001 : 这是一个日志输出。
     * 2016/01/08 17:46:55 : 001 : 这是一个日志输出。
     *
     * @param logFile       日志文件
     * @param batchId       处理编号
     * @param exceptionInfo 异常信息
     */
    public static void writeLog(String logFile, String batchId, String exceptionInfo) {
        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.JAPANESE);
        Object args[] = {df.format(new Date()), batchId, exceptionInfo};
        String fmtMsg = MessageFormat.format("{0} : {1} : {2}", args);
        try {
            File logfile = new File(logFile);
            if (!logfile.exists()) {
                logfile.createNewFile();
            }
            FileWriter fw = new FileWriter(logFile, true);
            fw.write(fmtMsg);
            fw.write(System.getProperty("line.separator"));
            fw.flush();
            fw.close();
        } catch (Exception e) {
        }
    }

    /**
     * 读取文件信息
     *
     * @param realPath 目标文件
     * @return 文件内容
     */
    public static String readTextFile(String realPath) throws Exception {
        File file = new File(realPath);
        if (!file.exists()) {
            System.out.println("File not exist!");
            return null;
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(realPath), "UTF-8"));
        String temp = "";
        String txt = "";
        while ((temp = br.readLine()) != null) {
            txt += temp;
        }
        br.close();
        return txt;
    }

    /**
     * 复制文件
     *
     * @param srcFile    源文件路径
     * @param targetFile 目标文件路径
     */
    public static void copyFile(String srcFile, String targetFile) throws IOException {
        File scrfile = new File(srcFile);
        if (checkExist(srcFile)) {
            FileInputStream fi = null;
            FileOutputStream fo = null;
            FileChannel in = null;
            FileChannel out = null;
            try {
                fi = new FileInputStream(srcFile);
                fo = new FileOutputStream(targetFile);
                in = fi.getChannel();
                out = fo.getChannel();

                in.transferTo(0, in.size(), out);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fi.close();
                    in.close();
                    fo.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 复制文件夹
     *
     * @param sourceDir String 源文件夹
     * @param destDir   String 目标路径
     */
    public static void copyDir(String sourceDir, String destDir) {
        File sourceFile = new File(sourceDir);
        String tempSource;
        String tempDest;
        String fileName;
        if (new File(destDir).getParentFile().isDirectory()) {
            new File(destDir).mkdirs();
        }
        File[] files = sourceFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            fileName = files[i].getName();
            tempSource = sourceDir + "/" + fileName;
            tempDest = destDir + "/" + fileName;
            if (files[i].isFile()) {
                try {
                    copyFile(tempSource, tempDest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                copyDir(tempSource, tempDest);
            }
        }
        sourceFile = null;
    }

    /**
     * 移动(重命名)文件
     *
     * @param srcFile    源文件路径
     * @param targetFile 目标文件路径
     */
    public static void renameFile(String srcFile, String targetFile) throws IOException {
        try {
            copyFile(srcFile, targetFile);
            deleteFromName(srcFile);
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param sFileName 文件路径
     * @return true - 存在、false - 不存在
     */
    public static boolean checkExist(String sFileName) {
        boolean result = false;
        try {
            File f = new File(sFileName);
            if (f.exists() && f.isFile()) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    /**
     * 得到文件大小
     *
     * @param sFileName 文件路径
     * @return 文件大小（单位byte），文件不存在返回0,异常返回-1
     */
    public static long getSize(String sFileName) {
        long lSize = 0;
        try {
            File f = new File(sFileName);
            if (f.exists()) {
                if (f.isFile() && f.canRead()) {
                    lSize = f.length();
                } else {
                    lSize = -1;
                }
            } else {
                lSize = 0;
            }
        } catch (Exception e) {
            lSize = -1;
        }
        return lSize;
    }

    /**
     * 删除文件
     *
     * @param sFileName 文件路径
     * @return 成功返回true，反之返回false
     */
    public static boolean deleteFromName(String sFileName) {
        boolean bReturn = true;
        try {
            File oFile = new File(sFileName);
            if (oFile.exists()) {
                boolean bResult = oFile.delete();
                if (bResult == false) {
                    bReturn = false;
                }
            } else {
                bReturn = false;
            }
        } catch (Exception e) {
            bReturn = false;
        }
        return bReturn;
    }

    public boolean deleteDirectory(String path) {
        SimpleFileVisitor<Path> finder = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir,
                                                     BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file,
                                             BasicFileAttributes attrs) throws IOException {
                files.add(file.toFile());
                return super.visitFile(file, attrs);
            }
        };
        return false;
    }

    /**
     * 删除指定目录及其中的所有内容。
     *
     * @param dir 要删除的目录
     * @return 删除成功时返回true，否则返回false。
     */
    public static boolean deleteDirectory(File dir) {
        if (!dir.exists()) {
            return false;
        }
        File[] entries = dir.listFiles();
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].isDirectory()) {
                if (!deleteDirectory(entries[i])) {
                    return false;
                }
            } else {
                if (!entries[i].delete()) {
                    return false;
                }
            }
        }
        if (!dir.delete()) {
            return false;
        }
        return true;
    }

    /**
     * 解压缩
     *
     * @param sToPath  解压后路径 （为null或空时解压到源压缩文件路径）
     * @param sZipFile 压缩文件路径
     */
    public static void unZip(String sToPath, String sZipFile) throws Exception {
        if (null == sToPath || ("").equals(sToPath.trim())) {
            File objZipFile = new File(sZipFile);
            sToPath = objZipFile.getParent();
        }
        ZipFile zfile = new ZipFile(sZipFile);
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                continue;
            }
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(sToPath, ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
    }

    /**
     * getRealFileName
     *
     * @param baseDir     Root Directory
     * @param absFileName absolute Directory File Name
     * @return java.io.File     Return file
     */
    private static File getRealFileName(String baseDir, String absFileName) throws Exception {
        File ret = null;
        List dirs = new ArrayList();
        StringTokenizer st = new StringTokenizer(absFileName, System.getProperty("file.separator"));
        while (st.hasMoreTokens()) {
            dirs.add(st.nextToken());
        }
        ret = new File(baseDir);
        if (dirs.size() > 1) {
            for (int i = 0; i < dirs.size() - 1; i++) {
                ret = new File(ret, (String) dirs.get(i));
            }
        }
        if (!ret.exists()) {
            ret.mkdirs();
        }
        ret = new File(ret, (String) dirs.get(dirs.size() - 1));
        return ret;
    }

    /**
     * 压缩文件夹
     *
     * @param srcPathName 欲压缩的文件夹
     * @param finalFile   压缩后的zip文件 (为null或“”时默认同欲压缩目录)
     * @param strIncludes 包括哪些文件或文件夹 eg:zip.setIncludes("*.java");（没有时可为null）
     * @param strExcludes 排除哪些文件或文件夹 （没有时可为null）
     */
    public static void zip(String srcPathName, String finalFile, String strIncludes, String strExcludes) {
        File srcdir = new File(srcPathName);
        if (!srcdir.exists()) {
            throw new RuntimeException(srcPathName + "不存在！");
        }
        if (finalFile == null || "".equals(finalFile)) {
            finalFile = srcPathName + ".zip";
        }
        File zipFile = new File(finalFile);
        Project prj = new Project();
        Zip zip = new Zip();
        zip.setProject(prj);
        zip.setDestFile(zipFile);
        FileSet fileSet = new FileSet();
        fileSet.setProject(prj);
        fileSet.setDir(srcdir);
        if (strIncludes != null && !"".equals(strIncludes)) {
            fileSet.setIncludes(strIncludes); //包括哪些文件或文件夹 eg:zip.setIncludes("*.java");
        }
        if (strExcludes != null && !"".equals(strExcludes)) {
            fileSet.setExcludes(strExcludes); //排除哪些文件或文件夹
        }
        zip.addFileset(fileSet);
        zip.execute();
    }
}
