/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.baggage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.testing.EqualsTester;
import io.opentelemetry.baggage.EntryMetadata.EntryTtl;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class EntryTest {

  private static final String KEY = "KEY";
  private static final String KEY_2 = "KEY2";
  private static final String VALUE = "VALUE";
  private static final String VALUE_2 = "VALUE2";
  private static final EntryMetadata METADATA_UNLIMITED_PROPAGATION =
      EntryMetadata.create(EntryTtl.UNLIMITED_PROPAGATION);
  private static final EntryMetadata METADATA_NO_PROPAGATION =
      EntryMetadata.create(EntryTtl.NO_PROPAGATION);

  @Test
  void testGetKey() {
    assertThat(Entry.create(KEY, VALUE, METADATA_UNLIMITED_PROPAGATION).getKey()).isEqualTo(KEY);
  }

  @Test
  void testGetEntryMetadata() {
    assertThat(Entry.create(KEY, VALUE, METADATA_NO_PROPAGATION).getEntryMetadata())
        .isEqualTo(METADATA_NO_PROPAGATION);
  }

  @Test
  void testEntryEquals() {
    new EqualsTester()
        .addEqualityGroup(
            Entry.create(KEY, VALUE, METADATA_UNLIMITED_PROPAGATION),
            Entry.create(KEY, VALUE, METADATA_UNLIMITED_PROPAGATION))
        .addEqualityGroup(Entry.create(KEY, VALUE_2, METADATA_UNLIMITED_PROPAGATION))
        .addEqualityGroup(Entry.create(KEY_2, VALUE, METADATA_UNLIMITED_PROPAGATION))
        .addEqualityGroup(Entry.create(KEY, VALUE, METADATA_NO_PROPAGATION))
        .testEquals();
  }

  @Test
  void testKeyMaxLength() {
    assertThat(Entry.MAX_KEY_LENGTH).isEqualTo(255);
  }

  @Test
  void create_AllowEntryKeyNameWithMaxLength() {
    char[] chars = new char[Entry.MAX_KEY_LENGTH];
    Arrays.fill(chars, 'k');
    String key = new String(chars);
    assertThat(Entry.create(key, "value", Entry.METADATA_UNLIMITED_PROPAGATION)).isNotNull();
  }

  @Test
  void create_DisallowEntryKeyNameOverMaxLength() {
    char[] chars = new char[Entry.MAX_KEY_LENGTH + 1];
    Arrays.fill(chars, 'k');
    String key = new String(chars);
    assertThrows(
        IllegalArgumentException.class,
        () -> Entry.create(key, "value", Entry.METADATA_UNLIMITED_PROPAGATION));
  }

  @Test
  void create_DisallowKeyUnprintableChars() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Entry.create("\2ab\3cd", "value", Entry.METADATA_UNLIMITED_PROPAGATION));
  }

  @Test
  void createString_DisallowKeyEmpty() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Entry.create("", "value", Entry.METADATA_UNLIMITED_PROPAGATION));
  }

  @Test
  void testValueMaxLength() {
    assertThat(Entry.MAX_VALUE_LENGTH).isEqualTo(255);
  }

  @Test
  void create_AllowEntryValueWithMaxLength() {
    char[] chars = new char[Entry.MAX_VALUE_LENGTH];
    Arrays.fill(chars, 'v');
    String value = new String(chars);
    assertThat(Entry.create("key", value, Entry.METADATA_UNLIMITED_PROPAGATION).getValue())
        .isEqualTo(value);
  }

  @Test
  void create_DisallowEntryValueOverMaxLength() {
    char[] chars = new char[Entry.MAX_VALUE_LENGTH + 1];
    Arrays.fill(chars, 'v');
    String value = new String(chars);
    assertThrows(
        IllegalArgumentException.class,
        () -> Entry.create("key", value, Entry.METADATA_UNLIMITED_PROPAGATION));
  }

  @Test
  void disallowEntryValueWithUnprintableChars() {
    String value = "\2ab\3cd";
    assertThrows(
        IllegalArgumentException.class,
        () -> Entry.create("key", value, Entry.METADATA_UNLIMITED_PROPAGATION));
  }
}
