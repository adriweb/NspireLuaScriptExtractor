// LuaScriptExtractor
// Adrien "Adriweb" Bertrand 2014
// TI-Planet.org  -  Inspired-Lua.org
// v1.1 - March 18th 2014

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

public final class LuaScriptExtractor {

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(obj);
        return b.toByteArray();
    }

    public static void main(String[] args) throws IOException, UnsupportedFlavorException {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clip.getContents(null);
        if (contents == null) {
            System.out.println("\n\nThe clipboard is empty.");
        } else {
            DataFlavor flavors[] = contents.getTransferDataFlavors();
            for (DataFlavor flavor : flavors) {

                if (flavor.getHumanPresentableName().contains("application/x-ti")) {
                    Object transferData = contents.getTransferData(flavor);
                    byte[] bytes = serialize(transferData);
                    String str = new String(bytes);


                    int scriptStartIdx = str.indexOf("<sc:script");

                    if (scriptStartIdx > -1) {

                        System.out.println("Lua script found.");

                        String scriptTitle = "";
                        String titleStrXML = "<sc:mde name=\"TITLE\"";
                        if (str.contains(titleStrXML)) {
                            scriptTitle += str;
                            scriptTitle = scriptTitle.substring(str.indexOf(titleStrXML) + titleStrXML.length());
                            scriptTitle = scriptTitle.substring(scriptTitle.indexOf(">")+1, scriptTitle.indexOf("</sc:mde>"));
                            System.out.println("Original script title : " + scriptTitle);
                        }

                        if (str.contains("name=\"PASSW\""))
                            System.out.println("This script is password-protected. Please respect copyrighted content if any!");

                        boolean hasCDATA = false;
                        if (str.contains("><![CDATA[")) {
                            System.out.println("(This script was made with OS 3.6+)");
                            hasCDATA = true;
                        }

                        System.out.println("Extracting lua source...");

                        str = str.substring(scriptStartIdx);
                        str = str.substring(str.indexOf(">") + (hasCDATA ? ("<![CDATA[").length()+1 : 1),
                                            str.indexOf((hasCDATA ? "]]>" : "</sc:script>"))-1); // only one newline at the end.

                        str = str.replace("&#13;", "")
                                .replaceAll("^(-- B!2r.*\n)", "")               // Because TI - don't ask.
                                .replace("&quot;", "\"").replace("&apos;", "'")
                                .replace("&gt;", ">").replace("&lt;", "<")
                                .replace("&amp;", "&");

                        str = "-- Lua source code extracted with LuaScriptExtractor by Adriweb\n-- tiplanet.org  -  inspired-lua.org\n\n"
                                + (scriptTitle.length()>0 ? ("-- Original script title : '" + scriptTitle + "'\n\n") : "") + str;

                        PrintWriter out = new PrintWriter("source.lua");
                        out.println(str);
                        out.close();

                        System.out.println("Done ! " + str.split("\r\n|\r|\n").length + " lines extracted into 'source.lua'.");

                    } else {
                        System.out.println("No Lua script found !");
                        break;
                    }


                } else {
                    System.out.println("Not a TI-Nspire clipboard content !");
                    break;
                }

            }
        }
    }
}
