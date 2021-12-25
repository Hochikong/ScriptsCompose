package me.ckho.scriptscompose.service.impl

import org.springframework.stereotype.Service

/**
 * Share data between spring components
 * */
@Service
class DataCache {
    // interrupt only cancel tasks has same TaskHash
    val needToInterruptTasks = mutableListOf<String>()
    // halt will cancel all groups which contain running task with the same TaskHash
    val needToHaltTasks = mutableListOf<String>()
}