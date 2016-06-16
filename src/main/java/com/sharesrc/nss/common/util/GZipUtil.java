/*
 * Copyright (c) Sharesrc 2016.
 */

package com.sharesrc.nss.common.util;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * The GZip utility.
 *
 * @author sou
 * @since 2013
 */
public class GZipUtil {

    public GZipUtil() {
    }

    public static String compress(String str) {
        String result = "";
        if (str != null && str.length() != 0) {
            ByteArrayOutputStream baos = null;
            GZIPOutputStream gzos = null;

            try {
                baos = new ByteArrayOutputStream();
                gzos = new GZIPOutputStream(baos);
                gzos.write(str.getBytes());
                gzos.finish();
                result = BHSUtil.bytesToHex(baos.toByteArray());
            } catch (IOException ex) {
                System.out.println("(IOException) Compress (method: gzip) the string failed!");
            } finally {
                try {
                    if (baos != null) {
                        baos.close();
                    }

                    baos = null;
                    if (gzos != null) {
                        gzos.close();
                    }

                    gzos = null;
                } catch (IOException ex) {
                }

            }

            return result;
        } else {
            return result;
        }
    }

    public static String decompress(String cstr) {
        String result = "";
        if (cstr != null && cstr.length() != 0) {
            GZIPInputStream gzis = null;
            InputStreamReader reader = null;
            BufferedReader in = null;

            try {
                gzis = new GZIPInputStream(new ByteArrayInputStream(BHSUtil.hexStringToByteArray(cstr)));
                reader = new InputStreamReader(gzis);
                in = new BufferedReader(reader);
                StringBuffer e = new StringBuffer();
                String tmp = null;

                while ((tmp = in.readLine()) != null) {
                    e.append(tmp + "\n");
                }

                result = e.toString().trim();
            } catch (IOException ex) {
                System.out.println("(IOException) Decompress (method: gzip) the string failed!");
            } finally {
                try {
                    if (gzis != null) {
                        gzis.close();
                    }

                    gzis = null;
                    if (reader != null) {
                        reader.close();
                    }

                    reader = null;
                    if (in != null) {
                        in.close();
                    }

                    in = null;
                } catch (IOException ex) {
                }

            }

            return result;
        } else {
            return result;
        }
    }
}
