/*
 * Copyright (C) 2003, 2004  Pascal Essiembre, Essiembre Consultant Inc.
 * 
 * This file is part of Essiembre ResourceBundle Editor.
 * 
 * Essiembre ResourceBundle Editor is free software; you can redistribute it 
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * Essiembre ResourceBundle Editor is distributed in the hope that it will be 
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with Essiembre ResourceBundle Editor; if not, write to the 
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, 
 * Boston, MA  02111-1307  USA
 */
package com.essiembre.eclipse.rbe.model.bundle;

import com.essiembre.eclipse.rbe.ui.preferences.RBEPreferences;

//TODO move this class or most of it to UI layer, or have Preferences in model

/**
 * Bundle-related utility methods. 
 * @author Pascal Essiembre (essiembre@users.sourceforge.net)
 * @version $Author$ $Revision$ $Date$
 */
public final class PropertiesParser {

    /** System line separator. */
    private static final String SYSTEM_LINE_SEPARATOR = 
            System.getProperty("line.separator");
    
    private static final String KEY_VALUE_SEPARATORS = "=:";

    
    /**
     * Constructor.
     */
    private PropertiesParser() {
        super();
    }

    /**
     * Parses a string and converts it to a <code>Bundle</code>.  The string is 
     * expected to match the documented structure of a properties file.
     * The returned bundle will have no <code>Locale</code> and no
     * <code>BundleGroup</code> associated to it.
     * @param properties the string containing the properties to parse
     * @return a new bundle
     */
    public static Bundle parse(String properties) {

        Bundle bundle = new Bundle();
        String[] lines = properties.split("\r\n|\r|\n");
        
        boolean doneWithFileComment = false;
        StringBuffer fileComment = new StringBuffer();
        StringBuffer lineComment = new StringBuffer();
        StringBuffer lineBuf = new StringBuffer();
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            lineBuf.setLength(0);
            lineBuf.append(line);
        
            int equalPosition = findKeyValueSeparator(line);// lineBuf.indexOf("=");
            boolean isRegularLine = line.matches("^[^#].*");
            boolean isCommentedLine = line.matches("^##[^#].*");
            
            // parse regular and commented lines
            if (equalPosition >= 1 && (isRegularLine || isCommentedLine)) {
                doneWithFileComment = true;
                String comment = "";
                if (lineComment.length() > 0) {
                    comment = lineComment.toString();
                    lineComment.setLength(0);
                }

                if (isCommentedLine) {
                    lineBuf.delete(0, 2); // remove ##
                    equalPosition -= 2;
                }
                while (lineBuf.lastIndexOf("\\") == lineBuf.length() -1) {
                    int lineBreakPosition = lineBuf.lastIndexOf("\\");
                    lineBuf.replace(
                            lineBreakPosition,
                            lineBreakPosition + 1, "");
                    if (++i <= lines.length) {
                        String wrappedLine = lines[i].trim();
                        if (isCommentedLine) {
                            lineBuf.append(wrappedLine.replaceFirst("^##", ""));
                        } else {
                            lineBuf.append(wrappedLine);
                        }
                    }
                }
                String key = lineBuf.substring(0, equalPosition).trim();
                String value = lineBuf.substring(equalPosition + 1).trim();
                if (RBEPreferences.getConvertEncodedToUnicode()) {
                    key = PropertiesParser.convertEncodedToUnicode(key);
                    value = PropertiesParser.convertEncodedToUnicode(value);
                } else {
                    value = value.replaceAll("\\\\r", "\r");
                    value = value.replaceAll("\\\\n", "\n");
                }
                bundle.addEntry(
                        new BundleEntry(key, value, comment, isCommentedLine));
            // parse comment line
            } else if (lineBuf.indexOf("#") == 0) {
                if (!doneWithFileComment) {
                    fileComment.append(lineBuf);
                    fileComment.append(SYSTEM_LINE_SEPARATOR);
                } else {
                    lineComment.append(lineBuf);
                    lineComment.append(SYSTEM_LINE_SEPARATOR);
                }
            // handle blank or unsupported line
            } else {
                doneWithFileComment = true;
            }
        }
        bundle.setComment(fileComment.toString());
        return bundle;
    }
    
    
    /**
     * Converts encoded &#92;uxxxx to unicode chars
     * and changes special saved chars to their original forms
     * This method was copied from <code>Properties.loadConvert(String)</code>.
     * @see java.util.Properties#loadConvert(java.lang.String)
     */
    public static String convertEncodedToUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);

        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\' && x + 5 <= len) {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                        case '0': case '1': case '2': case '3': case '4':
                        case '5': case '6': case '7': case '8': case '9':
                            value = (value << 4) + aChar - '0';
                            break;
                        case 'a': case 'b': case 'c':
                        case 'd': case 'e': case 'f':
                            value = (value << 4) + 10 + aChar - 'a';
                            break;
                        case 'A': case 'B': case 'C':
                        case 'D': case 'E': case 'F':
                            value = (value << 4) + 10 + aChar - 'A';
                            break;
                        default:
                            value = aChar;
                            System.err.println(
                                    "Malformed \\uxxxx encoding found in this "
                                  + "string: " + theString);
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }
    
    /**
     * Finds the separator symbol that separates keys and values.
     * @param str the string on which to find seperator
     * @return the separator index or -1 if no separator was found
     */
    private static int findKeyValueSeparator(String str) {
        int index;
        int length = str.length();
        for (index = 0; index < length; index++) {
            char currentChar = str.charAt(index);
            if (currentChar == '\\')
                index++;
            else if (KEY_VALUE_SEPARATORS.indexOf(currentChar) != -1)
                break;
        }
        return index;
    }
}
