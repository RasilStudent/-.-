import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileInfo {

    private String name;
    private long size;
    private String  creationTime;
    private String createdBy;

    public FileInfo(File file) {
        this.name = file.getName();
        this.size = calculateSize(file);
        this.creationTime = getCreationTime(file.toPath());
        this.createdBy = getCreatedBy(file.toPath());
    }

    private long calculateSize(File file) {
        if (file.isFile()) {
            return file.length();
        } else if (file.isDirectory()) {
            long size = 0;
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    size += calculateSize(f);
                }
            }
            return size;
        } else {
            return 0;
        }
    }

    private String getCreationTime(Path path) {
        try {
            BasicFileAttributeView view = Files.getFileAttributeView(path, BasicFileAttributeView.class);
            BasicFileAttributes attributes = view.readAttributes();
            FileTime creationTime = attributes.creationTime();

            // Преобразование FileTime в строку с помощью SimpleDateFormat
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            return dateFormat.format(new Date(creationTime.toMillis()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getCreatedBy(Path path) {
        try {
            FileOwnerAttributeView ownerView = Files.getFileAttributeView(path, FileOwnerAttributeView.class);
            UserPrincipal owner = ownerView.getOwner();
            return owner.getName();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        double sizeInMB = (double) size / (1024 * 1024 * 1024);
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedSize = df.format(sizeInMB);
        return "Name: " + name + "\nSize: " + formattedSize + " GB\nCreation Time: " +
                creationTime + "\nCreated By: " + createdBy + "\n";
    }
}
