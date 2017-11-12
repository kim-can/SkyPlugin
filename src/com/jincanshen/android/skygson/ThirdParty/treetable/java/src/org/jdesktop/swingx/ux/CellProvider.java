package com.jincanshen.android.skygson.ThirdParty.treetable.java.src.org.jdesktop.swingx.ux;

/**
 * Created by dim on 16/11/7.
 */
public interface CellProvider {

    String getCellTitle(int index);

    void setValueAt(int column, String text);
}
