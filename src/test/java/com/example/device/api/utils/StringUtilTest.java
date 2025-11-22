package com.example.device.api.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilTest {

    @Test
    void normalize_NullInput_ReturnsNull() {
        assertNull(StringUtil.normalize(null));
    }

    @Test
    void normalize_EmptyString_ReturnsNull() {
        assertNull(StringUtil.normalize(""));
    }

    @Test
    void normalize_WhitespaceOnly_ReturnsNull() {
        assertNull(StringUtil.normalize("   "));
    }

    @Test
    void normalize_TrimmedString_NoChanges() {
        assertEquals("Apple", StringUtil.normalize("Apple"));
    }

    @Test
    void normalize_StringWithWhitespace_TrimmedCorrectly() {
        assertEquals("Apple", StringUtil.normalize("  Apple  "));
    }

    @Test
    void normalize_StringBecomesEmptyAfterTrim_ReturnsNull() {
        assertNull(StringUtil.normalize("   \n\t   "));
    }

    @Test
    void normalize_StringWithInternalSpaces_NotAffected() {
        assertEquals("iPhone 12", StringUtil.normalize("  iPhone 12  "));
    }
}
