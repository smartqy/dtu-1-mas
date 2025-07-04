## 1. Success:

| NUM | level         | time   | cost |
|-----|---------------|--------|------|
| 1   | OnlyLast.lvl  | 0,510s | 15   |
| 2   | sadbois.lvl   | 0,268s | 24   |
| 3   | Raffaello.lvl | 0,282  | 20   |
| 4   | aMAzing.lvl   | 0,564  | 72   |
| 5   | planB.lvl     | 0,345  | 21   |
| 6   | Spds.lvl      | 0,103  | 13   |
| 7   | PIAF.lvl      | 0,383  | 37   |
| 8   | JarvisExe.lvl | 3,929  | 78   |
| 9   | AIAgents.lvl  | 2,882  | 32   |


## 3. 问题分析
### 3.1 box 不可达
```
YummAI.lvl -  ++ box 可达性问题
EulerPpl.lvl - box 可达性问题
SinbadAil.lvl - 有box 不可达 goal的问题
Qbit.lvl - 有box 不可达 goal的问题
JAMP.lvl - 有box 不可达 goal的问题
```

### 3.2 到达终点后挡住了其他agent的例子
```
MAIzeRun.lvl 有个当终点的挡路了（1挡住了0出路）
MABRY.lvl 0挡住了2
Roundbout.lvl 2挡住了1
```

### 3.3 A* 在OpenSpace下导向性差，导致low level无法出来
已经改成weighted A*了。即：给h(x) 设置权重2
优化方向：EPEA* -> 找一个导向性函数减少node的生成。

### 



## 2. Timeout 

| num | level          |
|-----|----------------|
| 1   | aiJoes.lvl     |
| 2   | StarGrid.lvl   |
| 3   | Flower.lvl     |
| 4   | MABRY.lvl      |
| 5   | Hashtag.lvl    |
| 6   | Cliff.lvl      |
| 7   | CompCheck.lvl  |
| 8   | SinbadAil.lvl  |
| 9   | Monad.lvl      |
| 10  | Bouncer.lvl    |
| 11  | Spiraling.lvl  |
| 12  | RainyDay.lvl   |
| 13  | Roundbout.lvl  |
| 14  | BfsFreaks.lvl  |
| 15  | MAIzeRun.lvl   |
| 16  | SALTY.lvl      |
| 17  | Marathon.lvl   |

## 3. OOM

| num | level         |
|-----|---------------|
| 1   | AAAFUN.lvl    |
| 2   | CphAirprt.lvl |
| 3   | CerealWif.lvl |
| 4   | PandorAI.lvl  |
| 5   | NameHere.lvl  |
| 6   | Greeders.lvl  |
| 7   | YSERIOUS.lvl  |
| 8   | AGACode.lvl   |
| 9   | Eighty.lvl    |
| 10  | Medibots.lvl  |
| 11  | UrBlocked.lvl |
| 12  | TBH.lvl       |

### 3.1 oom details
```
(base) ~/Desktop/dtu/semester1/course/Mas/searchclient/searchclient_java/out/production/searchclient_java git:[cbs_yanqing_may]
java -jar /Users/blackbear/Desktop/dtu/semester1/course/Mas/searchclient/server.jar -c "java searchclient.NewSearchClient 5" -l "/Users/blackbear/Desktop/dtu/semester1/course/Mas/searchclient/complevels/" -t 180 -o HighMountai2


AAAFUN.lvl
Maximum memory usage exceeded.
java.lang.OutOfMemoryError: Java heap space
        at searchclient.cbs.model.LowLevelState.<init>(LowLevelState.java:86)
        at searchclient.cbs.model.LowLevelState.generateChildState(LowLevelState.java:323)
        at searchclient.cbs.model.LowLevelState.expand(LowLevelState.java:164)
        at searchclient.cbs.algriothem.AStarRunner.findPath(AStarRunner.java:65)
        at searchclient.cbs.algriothem.CBSRunner.initRoot(CBSRunner.java:213)
        at searchclient.cbs.algriothem.CBSRunner.findSolution(CBSRunner.java:34)
        at searchclient.NewSearchClient.main(NewSearchClient.java:44)



CphAirprt.lvl
Maximum memory usage exceeded.
java.lang.OutOfMemoryError: Java heap space
        at searchclient.cbs.model.LowLevelState.<init>(LowLevelState.java:86)
        at searchclient.cbs.model.LowLevelState.generateChildState(LowLevelState.java:323)
        at searchclient.cbs.model.LowLevelState.expand(LowLevelState.java:164)
        at searchclient.cbs.algriothem.AStarRunner.findPath(AStarRunner.java:65)
        at searchclient.cbs.algriothem.CBSRunner.initRoot(CBSRunner.java:213)
        at searchclient.cbs.algriothem.CBSRunner.findSolution(CBSRunner.java:34)
        at searchclient.NewSearchClient.main(NewSearchClient.java:44)
Unable to solve level.
Aborting by time out: no

CerealWif.lvl
Maximum memory usage exceeded.
java.lang.OutOfMemoryError: Java heap space
        at java.base/java.lang.StringConcatHelper.newString(StringConcatHelper.java:387)
        at java.base/java.lang.invoke.DirectMethodHandle$Holder.invokeStatic(DirectMethodHandle$Holder)
        at java.base/java.lang.invoke.LambdaForm$MH/0x0000000800c08400.invoke(LambdaForm$MH)
        at java.base/java.lang.invoke.Invokers$Holder.linkToTargetMethod(Invokers$Holder)
        at searchclient.cbs.model.Box.<init>(Box.java:15)
        at searchclient.cbs.model.Box.deepCopy(Box.java:35)
        at searchclient.cbs.model.LowLevelState.<init>(LowLevelState.java:83)
        at searchclient.cbs.model.LowLevelState.generateChildState(LowLevelState.java:323)
        at searchclient.cbs.model.LowLevelState.expand(LowLevelState.java:164)
        at searchclient.cbs.algriothem.AStarRunner.findPath(AStarRunner.java:65)
        at searchclient.cbs.algriothem.CBSRunner.initRoot(CBSRunner.java:213)
        at searchclient.cbs.algriothem.CBSRunner.findSolution(CBSRunner.java:34)
        at searchclient.NewSearchClient.main(NewSearchClient.java:44)



PandorAI.lvl
Maximum memory usage exceeded.
java.lang.OutOfMemoryError: Java heap space
        at searchclient.cbs.model.LowLevelState.<init>(LowLevelState.java:86)
        at searchclient.cbs.model.LowLevelState.generateChildState(LowLevelState.java:323)
        at searchclient.cbs.model.LowLevelState.expand(LowLevelState.java:164)
        at searchclient.cbs.algriothem.AStarRunner.findPath(AStarRunner.java:65)
        at searchclient.cbs.algriothem.CBSRunner.initRoot(CBSRunner.java:213)
        at searchclient.cbs.algriothem.CBSRunner.findSolution(CBSRunner.java:34)
        at searchclient.NewSearchClient.main(NewSearchClient.java:44)


NameHere.lvl
Time to calculate cost map: 8741ms
Maximum memory usage exceeded.
java.lang.OutOfMemoryError: Java heap space
        at java.base/java.util.HashMap.newNode(HashMap.java:1901)
        at java.base/java.util.HashMap.putVal(HashMap.java:629)
        at java.base/java.util.HashMap.put(HashMap.java:610)
        at searchclient.cbs.model.LowLevelState.<init>(LowLevelState.java:83)
        at searchclient.cbs.model.LowLevelState.generateChildState(LowLevelState.java:323)
        at searchclient.cbs.model.LowLevelState.expand(LowLevelState.java:164)
        at searchclient.cbs.algriothem.AStarRunner.findPath(AStarRunner.java:65)
        at searchclient.cbs.algriothem.CBSRunner.initRoot(CBSRunner.java:213)
        at searchclient.cbs.algriothem.CBSRunner.findSolution(CBSRunner.java:34)
        at searchclient.NewSearchClient.main(NewSearchClient.java:44)



Greeders.lvl
Time to calculate cost map: 42ms
Maximum memory usage exceeded.
java.lang.OutOfMemoryError: Java heap space
        at java.base/java.util.HashMap.resize(HashMap.java:702)
        at java.base/java.util.HashMap.computeIfAbsent(HashMap.java:1198)
        at searchclient.cbs.model.LowLevelState.getHeuristic(LowLevelState.java:413)
        at searchclient.cbs.model.LowLevelState.getAStar(LowLevelState.java:453)
        at searchclient.cbs.model.LowLevelState$$Lambda$21/0x0000000800c0da28.apply(Unknown Source)
        at java.base/java.util.Comparator.lambda$comparing$77a9974f$1(Comparator.java:473)
        at java.base/java.util.Comparator$$Lambda$22/0x0000000800c47460.compare(Unknown Source)
        at java.base/java.util.Objects.compare(Objects.java:188)
        at searchclient.cbs.model.LowLevelState.compareTo(LowLevelState.java:458)
        at searchclient.cbs.algriothem.AStarFrontier$$Lambda$19/0x0000000800c0f570.compare(Unknown Source)
        at java.base/java.util.PriorityQueue.siftDownUsingComparator(PriorityQueue.java:710)
        at java.base/java.util.PriorityQueue.poll(PriorityQueue.java:585)
        at searchclient.cbs.algriothem.AStarFrontier.pop(AStarFrontier.java:27)
        at searchclient.cbs.algriothem.AStarRunner.findPath(AStarRunner.java:39)
        at searchclient.cbs.algriothem.CBSRunner.initRoot(CBSRunner.java:213)
        at searchclient.cbs.algriothem.CBSRunner.findSolution(CBSRunner.java:34)
        at searchclient.NewSearchClient.main(NewSearchClient.java:44)



YSERIOUS.lvl
Time to calculate cost map: 868ms
Maximum memory usage exceeded.
java.lang.OutOfMemoryError: Java heap space
        at searchclient.cbs.model.LowLevelState.<init>(LowLevelState.java:86)
        at searchclient.cbs.model.LowLevelState.generateChildState(LowLevelState.java:323)
        at searchclient.cbs.model.LowLevelState.expand(LowLevelState.java:164)
        at searchclient.cbs.algriothem.AStarRunner.findPath(AStarRunner.java:65)
        at searchclient.cbs.algriothem.CBSRunner.initRoot(CBSRunner.java:213)
        at searchclient.cbs.algriothem.CBSRunner.findSolution(CBSRunner.java:34)
        at searchclient.NewSearchClient.main(NewSearchClient.java:44)

AGACode.lvl
Time to calculate cost map: 413ms
Maximum memory usage exceeded.
java.lang.OutOfMemoryError: Java heap space
        at searchclient.cbs.model.LowLevelState.<init>(LowLevelState.java:86)
        at searchclient.cbs.model.LowLevelState.generateChildState(LowLevelState.java:323)
        at searchclient.cbs.model.LowLevelState.expand(LowLevelState.java:164)
        at searchclient.cbs.algriothem.AStarRunner.findPath(AStarRunner.java:65)
        at searchclient.cbs.algriothem.CBSRunner.initRoot(CBSRunner.java:213)
        at searchclient.cbs.algriothem.CBSRunner.findSolution(CBSRunner.java:34)
        at searchclient.NewSearchClient.main(NewSearchClient.java:44)



Eighty.lvl
Time to calculate cost map: 497ms
Parameter of B provided = 5. MA-CBS.
Maximum memory usage exceeded.
java.lang.OutOfMemoryError: Java heap space
        at searchclient.cbs.model.LowLevelState.<init>(LowLevelState.java:86)
        at searchclient.cbs.model.LowLevelState.generateChildState(LowLevelState.java:323)
        at searchclient.cbs.model.LowLevelState.expand(LowLevelState.java:164)
        at searchclient.cbs.algriothem.AStarRunner.findPath(AStarRunner.java:65)
        at searchclient.cbs.algriothem.CBSRunner.initRoot(CBSRunner.java:213)
        at searchclient.cbs.algriothem.CBSRunner.findSolution(CBSRunner.java:34)
        at searchclient.NewSearchClient.main(NewSearchClient.java:44)




Medibots.lvl
Time to calculate cost map: 1056ms
Maximum memory usage exceeded.
java.lang.OutOfMemoryError: Java heap space
        at searchclient.cbs.model.LowLevelState.getHeuristic(LowLevelState.java:400)
        at searchclient.cbs.model.LowLevelState.getAStar(LowLevelState.java:453)
        at searchclient.cbs.model.LowLevelState$$Lambda$21/0x0000000800c0da28.apply(Unknown Source)
        at java.base/java.util.Comparator.lambda$comparing$77a9974f$1(Comparator.java:473)
        at java.base/java.util.Comparator$$Lambda$22/0x0000000800c47460.compare(Unknown Source)
        at java.base/java.util.Objects.compare(Objects.java:188)
        at searchclient.cbs.model.LowLevelState.compareTo(LowLevelState.java:458)
        at searchclient.cbs.algriothem.AStarFrontier$$Lambda$19/0x0000000800c0f570.compare(Unknown Source)
        at java.base/java.util.PriorityQueue.siftDownUsingComparator(PriorityQueue.java:712)
        at java.base/java.util.PriorityQueue.poll(PriorityQueue.java:585)
        at searchclient.cbs.algriothem.AStarFrontier.pop(AStarFrontier.java:27)
        at searchclient.cbs.algriothem.AStarRunner.findPath(AStarRunner.java:39)
        at searchclient.cbs.algriothem.CBSRunner.initRoot(CBSRunner.java:213)
        at searchclient.cbs.algriothem.CBSRunner.findSolution(CBSRunner.java:34)
        at searchclient.NewSearchClient.main(NewSearchClient.java:44)


UrBlocked.lvl
Maximum memory usage exceeded.
java.lang.OutOfMemoryError: Java heap space
        at java.base/java.util.HashMap.resize(HashMap.java:702)
        at java.base/java.util.HashMap.computeIfAbsent(HashMap.java:1198)
        at searchclient.cbs.model.LowLevelState.getHeuristic(LowLevelState.java:413)
        at searchclient.cbs.model.LowLevelState.getAStar(LowLevelState.java:453)
        at searchclient.cbs.model.LowLevelState$$Lambda$21/0x0000000800c0da28.apply(Unknown Source)
        at java.base/java.util.Comparator.lambda$comparing$77a9974f$1(Comparator.java:473)
        at java.base/java.util.Comparator$$Lambda$22/0x0000000800c47460.compare(Unknown Source)
        at java.base/java.util.Objects.compare(Objects.java:188)
        at searchclient.cbs.model.LowLevelState.compareTo(LowLevelState.java:458)
        at searchclient.cbs.algriothem.AStarFrontier$$Lambda$19/0x0000000800c0f570.compare(Unknown Source)
        at java.base/java.util.PriorityQueue.siftDownUsingComparator(PriorityQueue.java:712)
        at java.base/java.util.PriorityQueue.poll(PriorityQueue.java:585)
        at searchclient.cbs.algriothem.AStarFrontier.pop(AStarFrontier.java:27)
        at searchclient.cbs.algriothem.AStarRunner.findPath(AStarRunner.java:39)
        at searchclient.cbs.algriothem.CBSRunner.initRoot(CBSRunner.java:213)

TBH.lvl
Maximum memory usage exceeded.
java.lang.OutOfMemoryError: Java heap space
        at java.base/java.util.HashMap.resize(HashMap.java:702)
        at java.base/java.util.HashMap.putVal(HashMap.java:661)
        at java.base/java.util.HashMap.put(HashMap.java:610)
        at java.base/java.util.HashSet.add(HashSet.java:221)
        at searchclient.cbs.algriothem.AStarFrontier.add(AStarFrontier.java:22)
        at searchclient.cbs.algriothem.AStarRunner.findPath(AStarRunner.java:67)
        at searchclient.cbs.algriothem.CBSRunner.initRoot(CBSRunner.java:213)
        at searchclient.cbs.algriothem.CBSRunner.findSolution(CBSRunner.java:34)
        at searchclient.NewSearchClient.main(NewSearchClient.java:44)
```