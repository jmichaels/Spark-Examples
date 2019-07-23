# Put your real credentials somewhere safer
password = "MYPA$$"

puts "SCPing Jar to gateway node."

# https://www.ssh.com/ssh/putty/putty-manuals/0.68/Chapter5.html
puts `C:\\Users\\John\\Dropbox\\Apps\\pscp.exe -pw #{password} \\Users\\John\\Downloads\\cities.csv root@jmichaels-1.gce.cloudera.com:/tmp`
puts `C:\\Users\\John\\Dropbox\\Apps\\pscp.exe -pw #{password} target\\spark-cookbook-mvn-1.0-SNAPSHOT.jar root@jmichaels-1.gce.cloudera.com:/tmp`

puts "SSHing to gateway node and running spark-submit."

# https://www.ssh.com/ssh/putty/putty-manuals/0.68/Chapter7.html#plink-usage
puts `C:\\Users\\John\\Dropbox\\Apps\\plink.exe -batch -pw #{password} root@jmichaels-1.gce.cloudera.com cat /tmp/cities.csv`
puts `C:\\Users\\John\\Dropbox\\Apps\\plink.exe -batch -pw #{password} root@jmichaels-1.gce.cloudera.com spark2-submit --class SimpleDataframeExample /tmp/spark-cookbook-mvn-1.0-SNAPSHOT.jar`


puts "Current dir:" + `dir`