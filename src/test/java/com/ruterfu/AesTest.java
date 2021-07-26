package com.ruterfu;

import com.ruterfu.thirdpkg.apache.codec.Base64;
import com.ruterfu.utils.RtUtil;
import com.ruterfu.utils.aes.AES;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class AesTest {
    @Test
    public void testShortKeyEncrypt() {
        int totalCount = 8;
        int totalSucceed = 0;
        for (int i = 0; i < totalCount; i++) {
            String key = RtUtil.random(RtUtil.randomNumBoundary(16, 19));
            AES aes = new AES(key);
            // 测试英文解码准确性
            int count = 0;
            int succeed = 0;
            for (int j = 0; j < 1200; j++) {
                String text = RtUtil.random(10240, true);
                String encrypted = aes.encryptStringToString(text);
                byte[] encryptedBytes = Base64.decodeBase64(encrypted);
                String originText = aes.decryptToString(encryptedBytes);
                count++;
                if(originText.equals(text)) {
                    succeed++;
                }
            }
            if(count == succeed) {
                totalSucceed ++;
            }
        }
        assert totalCount == totalSucceed;
    }
}
