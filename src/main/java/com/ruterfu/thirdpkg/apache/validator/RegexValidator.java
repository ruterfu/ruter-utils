//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ruterfu.thirdpkg.apache.validator;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValidator implements Serializable {
    private static final long serialVersionUID = -8832409930574867162L;
    private final Pattern[] patterns;

    public RegexValidator(String regex) {
        this(regex, true);
    }

    public RegexValidator(String regex, boolean caseSensitive) {
        this(new String[]{regex}, caseSensitive);
    }

    public RegexValidator(String[] regexs) {
        this(regexs, true);
    }

    public RegexValidator(String[] regexs, boolean caseSensitive) {
        if (regexs != null && regexs.length != 0) {
            this.patterns = new Pattern[regexs.length];
            int flags = caseSensitive ? 0 : 2;

            for(int i = 0; i < regexs.length; ++i) {
                if (regexs[i] == null || regexs[i].length() == 0) {
                    throw new IllegalArgumentException("Regular expression[" + i + "] is missing");
                }

                this.patterns[i] = Pattern.compile(regexs[i], flags);
            }

        } else {
            throw new IllegalArgumentException("Regular expressions are missing");
        }
    }

    public boolean isValid(String value) {
        if (value == null) {
            return false;
        } else {
            for(int i = 0; i < this.patterns.length; ++i) {
                if (this.patterns[i].matcher(value).matches()) {
                    return true;
                }
            }

            return false;
        }
    }

    public String[] match(String value) {
        if (value == null) {
            return null;
        } else {
            for(int i = 0; i < this.patterns.length; ++i) {
                Matcher matcher = this.patterns[i].matcher(value);
                if (matcher.matches()) {
                    int count = matcher.groupCount();
                    String[] groups = new String[count];

                    for(int j = 0; j < count; ++j) {
                        groups[j] = matcher.group(j + 1);
                    }

                    return groups;
                }
            }

            return null;
        }
    }

    public String validate(String value) {
        if (value == null) {
            return null;
        } else {
            for(int i = 0; i < this.patterns.length; ++i) {
                Matcher matcher = this.patterns[i].matcher(value);
                if (matcher.matches()) {
                    int count = matcher.groupCount();
                    if (count == 1) {
                        return matcher.group(1);
                    }

                    StringBuilder buffer = new StringBuilder();

                    for(int j = 0; j < count; ++j) {
                        String component = matcher.group(j + 1);
                        if (component != null) {
                            buffer.append(component);
                        }
                    }

                    return buffer.toString();
                }
            }

            return null;
        }
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("RegexValidator{");

        for(int i = 0; i < this.patterns.length; ++i) {
            if (i > 0) {
                buffer.append(",");
            }

            buffer.append(this.patterns[i].pattern());
        }

        buffer.append("}");
        return buffer.toString();
    }
}
