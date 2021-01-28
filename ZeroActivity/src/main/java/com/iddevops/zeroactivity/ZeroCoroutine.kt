package com.iddevops.zeroactivity

data class TaskEntity(
    var observer: () -> Boolean,
    var task: () -> Unit,
    var name: String
)

interface ZeroCoroutine{
    val zeroCoroutine:ZeroCoroutineInstance
    fun resumeCoroutine() = zeroCoroutine.resumeCoroutine()
    fun pauseCoroutine() = zeroCoroutine.pauseCoroutine()
    fun waitFor(observer:()->Boolean,task:()->Unit) = zeroCoroutine.waitFor(observer,task,null)
    fun waitFor(observer:()->Boolean,task:()->Unit,name:String?) = zeroCoroutine.waitFor(observer,task,name)
}

class ZeroCoroutineInstance {
    //Thread Process
    private val coroutine= Runnable {
        kotlin.run {

            while (isRunning && taskEntity.size!=0){
                Thread.sleep(30)

                taskEntity.forEach {
                    if(it.value.observer()){

                        it.value.task()

                        taskFinished.add(it.key)
                    }
                }


                taskFinished.forEach {
                    taskEntity.remove(it)
                }

                taskFinished.clear()

            }


            pauseCoroutine()
        }
    }

    //Register Task
    private var taskID:Short=0
    private fun generateTaskID(): Short {
        return ++taskID
    }
    fun waitFor(observer:()->Boolean,task:()->Unit,name:String?){
        val taskID = generateTaskID()

        //registering task
        this.taskEntity[taskID] = TaskEntity(
            observer,
            task,
            name ?: "Task$taskID"
        )

        //resume if nessesary
        if (!isRunning)
            resumeCoroutine()
    }

    //Tasks Map
    private var isRunning = false
    private val taskEntity:HashMap<Short,TaskEntity> = HashMap()
    private val taskFinished:ArrayList<Short> = ArrayList()

    //Lifecycle must implemented
    fun resumeCoroutine(){
        if (isRunning)
            return

        isRunning=true
        Thread(coroutine).start()
    }
    fun pauseCoroutine(){
        if (!isRunning)
            return
        else
            isRunning=false
    }
}
