package com.example.mypms.plugins;

public class Escape {
    public static String escape(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#39;");
                    break;
                case '-':
                    sb.append("&#45;");
                    break;
                case ' ':
                    sb.append("&nbsp;");
                    break;
                case '=':
                    sb.append("&#61;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }
}
