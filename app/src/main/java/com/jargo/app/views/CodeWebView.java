package com.jargo.app.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * CodeWebView - Custom WebView để hiển thị code với syntax highlighting
 * Sử dụng highlight.js từ CDN
 */
public class CodeWebView extends WebView {

    // Theme colors
    private static final String BACKGROUND_LIGHT = "#f8f9fa";
    private static final String BACKGROUND_DARK = "#1e1e1e";
    private static final String TEXT_LIGHT = "#24292e";
    private static final String TEXT_DARK = "#d4d4d4";
    
    private boolean isDarkTheme = false;
    private int bugLineNumber = -1;

    public CodeWebView(Context context) {
        super(context);
        init();
    }

    public CodeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CodeWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        
        // Disable scrollbars for cleaner look
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(true);
        
        // Transparent background
        setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * Set dark theme
     */
    public void setDarkTheme(boolean darkTheme) {
        this.isDarkTheme = darkTheme;
    }

    /**
     * Set line number to highlight as bug
     */
    public void setBugLineNumber(int lineNumber) {
        this.bugLineNumber = lineNumber;
    }

    /**
     * Display code with syntax highlighting
     * @param code The code to display
     * @param language Programming language (java, javascript, python, etc.)
     */
    public void displayCode(String code, String language) {
        if (code == null || code.isEmpty()) {
            code = "// No code to display";
        }
        
        String html = buildHtml(code, language);
        loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }

    /**
     * Display code with line numbers
     */
    public void displayCodeWithLineNumbers(String code, String language) {
        if (code == null || code.isEmpty()) {
            code = "// No code to display";
        }
        
        String html = buildHtmlWithLineNumbers(code, language);
        loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }

    /**
     * Build HTML with highlight.js
     */
    private String buildHtml(String code, String language) {
        String bgColor = isDarkTheme ? BACKGROUND_DARK : BACKGROUND_LIGHT;
        String textColor = isDarkTheme ? TEXT_DARK : TEXT_LIGHT;
        String theme = isDarkTheme ? "github-dark" : "github";
        String escapedCode = escapeHtml(code);
        
        return "<!DOCTYPE html>\n" +
               "<html>\n" +
               "<head>\n" +
               "    <meta charset=\"UTF-8\">\n" +
               "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\">\n" +
               "    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/" + theme + ".min.css\">\n" +
               "    <script src=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js\"></script>\n" +
               "    <style>\n" +
               "        * { margin: 0; padding: 0; box-sizing: border-box; }\n" +
               "        body {\n" +
               "            font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', monospace;\n" +
               "            font-size: 14px;\n" +
               "            line-height: 1.6;\n" +
               "            background-color: " + bgColor + ";\n" +
               "            color: " + textColor + ";\n" +
               "            padding: 16px;\n" +
               "            overflow-x: auto;\n" +
               "        }\n" +
               "        pre {\n" +
               "            margin: 0;\n" +
               "            white-space: pre;\n" +
               "            overflow-x: auto;\n" +
               "        }\n" +
               "        code {\n" +
               "            font-family: inherit;\n" +
               "            background: transparent !important;\n" +
               "            padding: 0 !important;\n" +
               "        }\n" +
               "        .hljs {\n" +
               "            background: transparent !important;\n" +
               "            padding: 0 !important;\n" +
               "        }\n" +
               "    </style>\n" +
               "</head>\n" +
               "<body>\n" +
               "    <pre><code class=\"language-" + language + "\">" + escapedCode + "</code></pre>\n" +
               "    <script>hljs.highlightAll();</script>\n" +
               "</body>\n" +
               "</html>";
    }

    /**
     * Build HTML with line numbers
     */
    private String buildHtmlWithLineNumbers(String code, String language) {
        String bgColor = isDarkTheme ? BACKGROUND_DARK : BACKGROUND_LIGHT;
        String textColor = isDarkTheme ? TEXT_DARK : TEXT_LIGHT;
        String lineNumColor = isDarkTheme ? "#6e7681" : "#8c959f";
        String theme = isDarkTheme ? "github-dark" : "github";
        String bugHighlightColor = isDarkTheme ? "rgba(248, 81, 73, 0.2)" : "rgba(255, 129, 130, 0.2)";
        
        // Split code into lines and add line numbers
        String[] lines = code.split("\n");
        StringBuilder numberedCode = new StringBuilder();
        
        for (int i = 0; i < lines.length; i++) {
            int lineNum = i + 1;
            String lineClass = (lineNum == bugLineNumber) ? "bug-line" : "";
            String escapedLine = escapeHtml(lines[i]);
            
            numberedCode.append("<tr class=\"").append(lineClass).append("\">");
            numberedCode.append("<td class=\"line-num\">").append(lineNum).append("</td>");
            numberedCode.append("<td class=\"line-code\"><code>").append(escapedLine).append("</code></td>");
            numberedCode.append("</tr>\n");
        }
        
        return "<!DOCTYPE html>\n" +
               "<html>\n" +
               "<head>\n" +
               "    <meta charset=\"UTF-8\">\n" +
               "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\">\n" +
               "    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/" + theme + ".min.css\">\n" +
               "    <script src=\"https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js\"></script>\n" +
               "    <style>\n" +
               "        * { margin: 0; padding: 0; box-sizing: border-box; }\n" +
               "        body {\n" +
               "            font-family: 'JetBrains Mono', 'Fira Code', 'Consolas', monospace;\n" +
               "            font-size: 13px;\n" +
               "            line-height: 1.5;\n" +
               "            background-color: " + bgColor + ";\n" +
               "            color: " + textColor + ";\n" +
               "            overflow-x: auto;\n" +
               "        }\n" +
               "        table {\n" +
               "            border-collapse: collapse;\n" +
               "            width: 100%;\n" +
               "        }\n" +
               "        tr.bug-line {\n" +
               "            background-color: " + bugHighlightColor + ";\n" +
               "        }\n" +
               "        .line-num {\n" +
               "            color: " + lineNumColor + ";\n" +
               "            text-align: right;\n" +
               "            padding: 0 12px 0 8px;\n" +
               "            user-select: none;\n" +
               "            vertical-align: top;\n" +
               "            border-right: 1px solid " + lineNumColor + "40;\n" +
               "            min-width: 40px;\n" +
               "        }\n" +
               "        .line-code {\n" +
               "            padding-left: 12px;\n" +
               "            white-space: pre;\n" +
               "        }\n" +
               "        code {\n" +
               "            font-family: inherit;\n" +
               "            background: transparent !important;\n" +
               "        }\n" +
               "        .hljs {\n" +
               "            background: transparent !important;\n" +
               "            padding: 0 !important;\n" +
               "        }\n" +
               "    </style>\n" +
               "</head>\n" +
               "<body>\n" +
               "    <table>\n" +
               "        <tbody class=\"language-" + language + "\">\n" +
               numberedCode.toString() +
               "        </tbody>\n" +
               "    </table>\n" +
               "    <script>\n" +
               "        document.querySelectorAll('code').forEach((el) => {\n" +
               "            hljs.highlightElement(el);\n" +
               "        });\n" +
               "    </script>\n" +
               "</body>\n" +
               "</html>";
    }

    /**
     * Escape HTML special characters
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * Clear the code display
     */
    public void clear() {
        loadDataWithBaseURL(null, "", "text/html", "UTF-8", null);
    }
}
