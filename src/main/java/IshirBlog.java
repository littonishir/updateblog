import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class IshirBlog {

    private static Date timeZone;
    public static void main(String[] args) throws IOException {
        /**
         * sed_path  符号净化脚本
         * post_path 更新博客脚本
         * article_path 文章路径
         */
        String sed_path = "/Users/ishirlitton/Code/scripts/sed.sh";
        String post_path = "/Users/ishirlitton/Code/scripts/blog_post.sh";
        String article_path = "/Users/ishirlitton/Code/Blog/littonishir.github.com/_posts";


        long current = System.currentTimeMillis() / (1000 * 3600 * 24) * (1000 * 3600 * 24);
        getCurrentTimeZone(current);

        System.out.println("脚本开始执行");
        getFiles(article_path).forEach(file -> {
            String shell = sed_path + " " + file.getName();
            execShell(shell);
            System.out.println("脚本执行中···");
        });
        execShell(post_path);
        System.out.println("脚本执行完毕");

    }

    /**
     * 获取目录下所有文件
     *
     * @param realPath
     * @return
     */
    public static ArrayList<File> getFiles(String realPath) throws IOException {
        ArrayList<File> files = new ArrayList<>();
        File realFile = new File(realPath);
        if (realFile.isDirectory()) {
            File[] subFiles = realFile.listFiles();
            for (File file : subFiles) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath());
                } else {
                    //获取文件的属性
                    Path path = Paths.get(file.getAbsolutePath());
                    BasicFileAttributes basicFileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
                    Date date = new Date(basicFileAttributes.lastModifiedTime().toMillis());
                    boolean after = timeZone.before(date);
                    if (after) {
                        files.add(file);
                    }
                }
            }
        }
        return files;
    }

    /**
     * 获取当天零点零分零秒的毫秒数
     *
     * @param currentTime
     */
    private static void getCurrentTimeZone(long currentTime) {
        // 获取“时间偏移”。相对于“本初子午线”的偏移，单位是ms。
        long rawOffset = TimeZone.getDefault().getRawOffset();
        long zero = currentTime - rawOffset;
        timeZone = new Date(zero);
    }

    /**
     * 执行shell脚本
     *
     * @param path
     */
    private static void execShell(String path) {
        try {
            Process ps = Runtime.getRuntime().exec(path);
            ps.waitFor();
            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            System.out.println(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

