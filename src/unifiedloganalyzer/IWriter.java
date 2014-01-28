/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package unifiedloganalyzer;

/**
 *
 * @author CZ2B1142
 */
public interface IWriter {
    void write(ParsedData data);
    void flush();
    void eof();
}
