//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ruterfu.third_pkg.apache.validator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InetAddressValidator implements Serializable {
    private static final int IPV4_MAX_OCTET_VALUE = 255;
    private static final int MAX_UNSIGNED_SHORT = 65535;
    private static final int BASE_16 = 16;
    private static final long serialVersionUID = -919201640201914789L;
    private static final String IPV4_REGEX = "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$";
    private static final int IPV6_MAX_HEX_GROUPS = 8;
    private static final int IPV6_MAX_HEX_DIGITS_PER_GROUP = 4;
    private static final InetAddressValidator VALIDATOR = new InetAddressValidator();
    private final RegexValidator ipv4Validator = new RegexValidator("^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$");

    public InetAddressValidator() {
    }

    public static InetAddressValidator getInstance() {
        return VALIDATOR;
    }

    public boolean isValid(String inetAddress) {
        return this.isValidInet4Address(inetAddress) || this.isValidInet6Address(inetAddress);
    }

    public boolean isValidInet4Address(String inet4Address) {
        String[] groups = this.ipv4Validator.match(inet4Address);
        if (groups == null) {
            return false;
        } else {
            String[] arr$ = groups;
            int len$ = groups.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                String ipSegment = arr$[i$];
                if (ipSegment == null || ipSegment.length() == 0) {
                    return false;
                }

                boolean var7 = false;

                int iIpSegment;
                try {
                    iIpSegment = Integer.parseInt(ipSegment);
                } catch (NumberFormatException var9) {
                    return false;
                }

                if (iIpSegment > 255) {
                    return false;
                }

                if (ipSegment.length() > 1 && ipSegment.startsWith("0")) {
                    return false;
                }
            }

            return true;
        }
    }

    public boolean isValidInet6Address(String inet6Address) {
        String[] parts = inet6Address.split("/", -1);
        if (parts.length > 2) {
            return false;
        } else {
            if (parts.length == 2) {
                if (!parts[1].matches("\\d{1,3}")) {
                    return false;
                }

                int bits = Integer.parseInt(parts[1]);
                if (bits < 0 || bits > 128) {
                    return false;
                }
            }

            parts = parts[0].split("%", -1);
            if (parts.length > 2) {
                return false;
            } else if (parts.length == 2 && !parts[1].matches("[^\\s/%]+")) {
                return false;
            } else {
                inet6Address = parts[0];
                boolean containsCompressedZeroes = inet6Address.contains("::");
                if (containsCompressedZeroes && inet6Address.indexOf("::") != inet6Address.lastIndexOf("::")) {
                    return false;
                } else if ((!inet6Address.startsWith(":") || inet6Address.startsWith("::")) && (!inet6Address.endsWith(":") || inet6Address.endsWith("::"))) {
                    String[] octets = inet6Address.split(":");
                    if (containsCompressedZeroes) {
                        List<String> octetList = new ArrayList(Arrays.asList(octets));
                        if (inet6Address.endsWith("::")) {
                            octetList.add("");
                        } else if (inet6Address.startsWith("::") && !octetList.isEmpty()) {
                            octetList.remove(0);
                        }

                        octets = (String[])octetList.toArray(new String[octetList.size()]);
                    }

                    if (octets.length > 8) {
                        return false;
                    } else {
                        int validOctets = 0;
                        int emptyOctets = 0;

                        for(int index = 0; index < octets.length; ++index) {
                            String octet = octets[index];
                            if (octet.length() == 0) {
                                ++emptyOctets;
                                if (emptyOctets > 1) {
                                    return false;
                                }
                            } else {
                                emptyOctets = 0;
                                if (index == octets.length - 1 && octet.contains(".")) {
                                    if (!this.isValidInet4Address(octet)) {
                                        return false;
                                    }

                                    validOctets += 2;
                                    continue;
                                }

                                if (octet.length() > 4) {
                                    return false;
                                }

                                boolean var9 = false;

                                int octetInt;
                                try {
                                    octetInt = Integer.parseInt(octet, 16);
                                } catch (NumberFormatException var11) {
                                    return false;
                                }

                                if (octetInt < 0 || octetInt > 65535) {
                                    return false;
                                }
                            }

                            ++validOctets;
                        }

                        if (validOctets <= 8 && (validOctets >= 8 || containsCompressedZeroes)) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                } else {
                    return false;
                }
            }
        }
    }
}
