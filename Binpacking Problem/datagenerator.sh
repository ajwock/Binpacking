PROGRAM="./runscript.sh"

maxSize=$2
stepSize=$3
dataPerSize=$4
output=$1
runtype=$5
nl=$'\n'

rm output
for ((i = $stepSize; i <= $maxSize; i += $stepSize))
do
  "/bin/echo" -n $i >> $output
  "/bin/echo" -n $i
  for ((j = 1; j <= $dataPerSize; j++))
  do
    filename="testfiles/temp/test${i}_no${j}.bp"
    python "testfiles/generateInstance.py" $filename $i
    runtime=$(timeout 100s $PROGRAM $filename --runtime $runtype \
                || echo "100000")
    "/bin/echo" -n ",$runtime"
    "/bin/echo" -n ",$runtime" >> $output
  done
  "/bin/echo" -n "$nl"
  "/bin/echo" -n "$nl" >> $output
done
