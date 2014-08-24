$subscriptionName = "Azpad255CPE6727"   
$clusterName = "ningxin"    

# Define the word count MapReduce job
$wordCountJobDefinition = New-AzureHDInsightMapReduceJobDefinition `
        -JarFile "wasb:///example/jars/pscan.jar" `
        -ClassName "com.ibm.pscan.control.FormatInputTwo"

Select-AzureSubscription $subscriptionName

$wordCountJob = Start-AzureHDInsightJob `
        -Cluster $clusterName `
        -JobDefinition $wordCountJobDefinition 

# Wait for the job to complete
Wait-AzureHDInsightJob -Job $wordCountJob -WaitTimeoutInSeconds 3600 

# Get the job standard error output
Get-AzureHDInsightJobOutput -Cluster $clusterName -JobId $wordCountJob.JobId -StandardError 