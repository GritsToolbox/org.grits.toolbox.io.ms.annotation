package org.grits.toolbox.io.ms.annotation.process;

public interface GRITSConverterListener
{
    public void error(String a_message, Exception a_exception, Integer a_scan, String a_glycanId);

    public void error(String a_message, Integer a_scan);

    public void processScan(Integer a_scan);

    public void error(String a_message, Exception a_exception, Integer a_scan);

    public void error(String a_message);

}
