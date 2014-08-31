$subscriptionName = "Azpad255CPE6727"   
$clusterName = "ningxin"    

# Define the word count MapReduce job
$formatJobDefinition = New-AzureHDInsightMapReduceJobDefinition `
        -JarFile "wasb:///example/jars/pscan.jar" `
        -ClassName "com.ibm.pscan.control.FormatInputTwo"

Select-AzureSubscription $subscriptionName

$formatJob = Start-AzureHDInsightJob `
        -Cluster $clusterName `
        -JobDefinition $formatJobDefinition 

# Wait for the job to complete
Wait-AzureHDInsightJob -Job $formatJob -WaitTimeoutInSeconds 3600 

# Get the job standard error output
Get-AzureHDInsightJobOutput -Cluster $clusterName -JobId $wordCountJob.JobId -StandardError 