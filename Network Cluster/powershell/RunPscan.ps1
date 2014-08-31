$subscriptionName = "Azpad255CPE6727"   
$clusterName = "ningxin"    

# Define the word count MapReduce job
$pscanJobDefinition = New-AzureHDInsightMapReduceJobDefinition `
        -JarFile "wasb:///example/jars/pscan.jar" `
        -ClassName "com.ibm.pscan.control.PSCAN" `
        -Arguments "wasb:///example/pscan/userRelation.txt", "wasb:///example/data/pscanOutput","0.82"

Select-AzureSubscription $subscriptionName

$pscanJob = Start-AzureHDInsightJob `
        -Cluster $clusterName `
        -JobDefinition $pscanJobDefinition 

# Wait for the job to complete
Wait-AzureHDInsightJob -Job $pscanJob -WaitTimeoutInSeconds 3600 

# Get the job standard error output
Get-AzureHDInsightJobOutput -Cluster $clusterName -JobId $pscanJob.JobId -StandardError 