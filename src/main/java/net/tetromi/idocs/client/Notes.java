package net.tetromi.idocs.client;

import java.util.AbstractList;
import java.util.List;

/** wraps a list of notes with added (de)serialization functionality. */
public class Notes extends AbstractList<Note> implements List<Note> {
    @Override public Note get(int index) {
        return null;
    }
    @Override public int size() {
        return 0;
    }
}
