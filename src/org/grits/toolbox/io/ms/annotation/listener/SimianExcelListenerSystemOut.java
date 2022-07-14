package org.grits.toolbox.io.ms.annotation.listener;

public class SimianExcelListenerSystemOut implements ExcelListener
{

    public void message(String a_message)
    {
        System.out.println(a_message);
    }

}
