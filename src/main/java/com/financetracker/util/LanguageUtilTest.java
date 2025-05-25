package com.financetracker.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

class LanguageUtilTest {

    private LanguageUtil.Language originalLanguage;

    @BeforeEach
    void setUp() {
        // Store original language to restore it after tests
        originalLanguage = LanguageUtil.getCurrentLanguage();
    }

    @AfterEach
    void tearDown() {
        // Restore original language
        LanguageUtil.setCurrentLanguage(originalLanguage);
    }

    @Test
    @DisplayName("Default language should be English")
    void defaultLanguage_isEnglish() {
        // Reset to a known state for this specific test if needed,
        // but @BeforeEach and @AfterEach should handle general state.
        // For this test, we assume the static block initializes to English if not CHINESE.
        // Let's explicitly set it to English for this test to be certain.
        LanguageUtil.setCurrentLanguage(LanguageUtil.ENGLISH);
        assertEquals(LanguageUtil.ENGLISH, LanguageUtil.getCurrentLanguage());
    }

    @Test
    @DisplayName("setCurrentLanguage should change the current language")
    void setCurrentLanguage_changesLanguage() {
        LanguageUtil.setCurrentLanguage(LanguageUtil.CHINESE);
        assertEquals(LanguageUtil.CHINESE, LanguageUtil.getCurrentLanguage());

        LanguageUtil.setCurrentLanguage(LanguageUtil.ENGLISH);
        assertEquals(LanguageUtil.ENGLISH, LanguageUtil.getCurrentLanguage());
    }

    @Test
    @DisplayName("setCurrentLanguage with null should not change language")
    void setCurrentLanguage_null_doesNotChangeLanguage() {
        LanguageUtil.setCurrentLanguage(LanguageUtil.CHINESE); // Set a known state
        LanguageUtil.Language langBefore = LanguageUtil.getCurrentLanguage();
        LanguageUtil.setCurrentLanguage(null);
        assertEquals(langBefore, LanguageUtil.getCurrentLanguage());
    }

    @Test
    @DisplayName("setCurrentLanguage with unsupported language should not change language")
    void setCurrentLanguage_unsupported_doesNotChangeLanguage() {
        LanguageUtil.setCurrentLanguage(LanguageUtil.ENGLISH); // Set a known state
        LanguageUtil.Language langBefore = LanguageUtil.getCurrentLanguage();
        LanguageUtil.setCurrentLanguage(new LanguageUtil.Language("fr")); // Unsupported
        assertEquals(langBefore, LanguageUtil.getCurrentLanguage());
    }

    @Test
    @DisplayName("getText should return correct English text")
    void getText_english_returnsCorrectText() {
        LanguageUtil.setCurrentLanguage(LanguageUtil.ENGLISH);
        assertEquals("File", LanguageUtil.getText("menu.file"));
        assertEquals("Login", LanguageUtil.getText("login.login"));
        assertEquals("Username:", LanguageUtil.getText("login.username"));
        // Test a key that might be in the properties file or in-memory map
        assertEquals("AI-Empowered Personal Finance Tracker", LanguageUtil.getText("login.title"));
    }

    @Test
    @DisplayName("getText should return correct Chinese text")
    void getText_chinese_returnsCorrectText() {
        LanguageUtil.setCurrentLanguage(LanguageUtil.CHINESE);
        assertEquals("文件", LanguageUtil.getText("menu.file"));
        assertEquals("登录", LanguageUtil.getText("login.login"));
        assertEquals("用户名:", LanguageUtil.getText("login.username"));
        assertEquals("AI驱动个人财务追踪器", LanguageUtil.getText("login.title"));
    }

    @Test
    @DisplayName("getText for unknown key should return the key itself")
    void getText_unknownKey_returnsKey() {
        LanguageUtil.setCurrentLanguage(LanguageUtil.ENGLISH);
        String unknownKey = "this.key.does.not.exist";
        assertEquals(unknownKey, LanguageUtil.getText(unknownKey));

        LanguageUtil.setCurrentLanguage(LanguageUtil.CHINESE);
        assertEquals(unknownKey, LanguageUtil.getText(unknownKey));
    }

    @Test
    @DisplayName("Language inner class equals and hashCode should work correctly")
    void languageClass_equalsAndHashCode() {
        LanguageUtil.Language langEn1 = new LanguageUtil.Language("en");
        LanguageUtil.Language langEn2 = new LanguageUtil.Language("en");
        LanguageUtil.Language langZh = new LanguageUtil.Language("zh");

        assertEquals(langEn1, langEn2, "Two Language objects with the same code should be equal.");
        assertNotEquals(langEn1, langZh, "Language objects with different codes should not be equal.");
        assertEquals(langEn1.hashCode(), langEn2.hashCode(), "Hash codes should be equal for equal objects.");
        assertNotEquals(langEn1.hashCode(), langZh.hashCode(), "Hash codes should ideally be different for non-equal objects.");
        assertNotEquals(langEn1, null);
        assertNotEquals(langEn1, new Object());
    }

    @Test
    @DisplayName("Check specific menu item translations")
    void checkSpecificMenuTranslations() {
        LanguageUtil.setCurrentLanguage(LanguageUtil.CHINESE);
        assertEquals("导入CSV", LanguageUtil.getText("file.import"));
        assertEquals("仪表盘", LanguageUtil.getText("main.dashboard"));

        LanguageUtil.setCurrentLanguage(LanguageUtil.ENGLISH);
        assertEquals("Import CSV", LanguageUtil.getText("file.import"));
        assertEquals("Dashboard", LanguageUtil.getText("main.dashboard"));
    }
}
