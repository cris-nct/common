package util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class VBMessageBox {

    private final String appName;

    public VBMessageBox(String appName) {
        this.appName = appName;
    }

    public void displayErrorMessage(String msg, boolean waitToClose) {
        displayMessage(16, msg, waitToClose);
    }

    public void displayWarningMessage(String msg, boolean waitToClose) {
        displayMessage(48, msg, waitToClose);
    }

    public void displayInfoMessage(String msg, boolean waitToClose) {
        displayMessage(64, msg, waitToClose);
    }

    public void displayMessage(int icon, String msg, boolean waitToClose) {
        log.error(msg);
        File file = new File("error" + System.currentTimeMillis() + ".vbs");
        try (FileWriter writer = new FileWriter(file)) {
            writer.append(String.format("msgbox %s, %d+Icon,\"%s\"", convertToVBmultiline(msg), icon, appName));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        file.deleteOnExit();
        try {
            Process process = Runtime.getRuntime().exec("cmd /c " + file.getAbsolutePath());
            if (waitToClose) {
                process.waitFor();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String convertToVBmultiline(String msg) {
        String[] parts = msg.split("\n");
        StringBuilder msgVBbuilder = new StringBuilder();
        for (String part : parts) {
            if (msgVBbuilder.length() > 0) {
                msgVBbuilder.append("& vbCrLf &");
            }
            msgVBbuilder.append("\"");
            msgVBbuilder.append(part);
            msgVBbuilder.append("\"");
        }
        return msgVBbuilder.toString();
    }

}
