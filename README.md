PSCAN-for-MSc-Project
=====================

Implemented the PSCAN algorithm in Xu et al. (2013) on Azure HDInsight. More features are added to help the algorithm fit for the yammer social network. This project is written for IBM and UCL for finding the hubs and outliners of the yammer enterprise social network

Copyright owned by Ningxin Cui, if anyone want to use this code, please contact the author first. Otherwise, it will violate the copyright. The copyright of the external libraries are owned by the realted user. Thanks for "Microsoft Azure", "Cloud9" and "Apache Hadoop". 

The email address of the author: cuiningxin@gmail.com

----------------------------------------------------------------------------------------------------------------------------

<b>How to use this program: </b>

Download the zip file, find "Config.java" and change the access_token to the specific token of your company. Using eclipse and Hadoop commond line (provide by azure hadoop) generate a runnable jar, name it as pscan.jar. Upload the pscan.jar to the relative azure account through "UploadJar.java". (the example about how to generate a map-reduce runnable jar could be found at the end of this file)

Find the powershell folder. Download and configure the azure powershell. 

Link for download: http://www.microsoft.com/en-gb/download/details.aspx?id=40855
Link for configuration: http://azure.microsoft.com/en-us/documentation/articles/install-configure-powershell/ 

Once completed, open the "powershell.ise", open the "RunFormatInput.ps1" and "RunPscan.ps1", change the subscriptionName and clusterName to your own azure hdinsight account.

Once finished the previous steps, you could start to find the special roles (the one who spreading ideas).

----------------------------------------------------------------------------------------------------------------------------
<b> Analysis the Special Roles on Yammer: </b>

  1. Run "FormatInputOne.java" (this will upload the realted private conversational data to azure hadoop)(If the yammer network denies the conncetion, this will return some errors. In this case, please check the token in Config.java again. If it still do not work, please contact yammer.)

  2. If you want to analyse the post messages at the same time, then open and run "RunFormatInput.ps1" in "powershell.ise". If you do not want to analyse the post messages, skip this step.
  
  3. Change the third argument in "RunPscan.ps1" to "0.0", run it in "powershell.ise"

  4. Once completed, run "DrawInputPara.java", see the graph and choose an appropriate value of the threshold. The value is always near the knee of the graph.
  
  5. Change the third argument in "RunPscan.ps1" to the seleted threshold value. Run the program. Once finished, run "ResultTFIDF.java". The cluster result could be found in "/pscan/result.csv" and the related TF-IDF results could be found in the same directory.
  
------------------------------------------------------------------------------------------------------------------------------
<b>Generate .jar file:</b>

  1. Import the project in eclipse, right click and select "export" -> "runnable JAR File" 
  
  2. Choose the export destination, selected "package required libraries into generated JAR". Click Finish.
  
  3. Uncompress the jar file, copy it to "c://Tutorials", if you do not have this folder, generate one.

  4. Run "hadoop commond line", copy these two lines:
      cd ../../tutorials/pscan
      C:\Hadoop\java\bin\jar -cvf pscan.jar *.*

  5. Find the pscan.jar file, copy it to the directory same as the one in "UploadJar.java". Run "UploadJar.java".
  
  6. The pscan.jar has already upload to your azure hdinsight.
