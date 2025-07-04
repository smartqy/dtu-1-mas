# Issues

## 1. How to group agent and boxes into a single task(represent that one agent with several boxes)

    Background: there is a possibility that more than one agent can push more than one boxes. So how to group them into a single task?
    Current implementation: currently, we use static assignment.

## 2. How to assign the goal location to box?

    Background: one box-letter may have multiple boxes. So how to assign the goal location to box?
    Current implementation: currently, we use static assignment.

## 3. The end state of a sub-task blocks other tasks - have fixed by MA-CBS

    Background: refer to MAsimple4.lvl. The end state of a sub-task blocks other tasks
    Current implementation: currently, we do not solve this problem. After a sub-task is completed, its subsequent operations are all Noop. Even if a conflict is detected, only one-sided constraints are added to another sub-task. It
    will not re-plan itself. So it will always block the road.

## 4. The inter-blocking problem is temporarily unsolved - have fixed by MA-CBS

    Background: refer to MAsimple3.lvl, MAsimple2.lvl

# Issues in Chinese

## 1. 子任务分组问题

背景：agent 和 boxes如何分组
当前实现：目前采用静态分配。

## 2. box 分配 目标问题

背景- 一个box-letter 可能有多个 boxes。所以如何分配box的目标给到box？
当前实现：静态分配。

## 2. 一个子任务的终态挡住了其他任务

参考 MAsimple4.lvl
背景 - .一个子任务的终态挡住了其他任务
当前实现：暂未解决。一个子任务完成后，它的后续操作都是Noop。即使检测到冲突也只是单边的给另一个子任务加constraint。自己不会重新规划。所以会一直挡道

## 3. 挡道的问题，暂时未解决

参考 MAsimple3.lvl,MAsimple2.lvl

