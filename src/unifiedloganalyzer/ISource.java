/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package unifiedloganalyzer;

import java.util.Iterator;

/**
 *
 * @author CZ2B1142
 */
public interface ISource extends Iterator<String> {
    @Override
    public boolean hasNext();
    @Override
    public String next();
    @Override
    public void remove();
}
