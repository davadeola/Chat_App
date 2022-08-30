package com.example.chatapp.models

import java.sql.Timestamp
import java.util.*

data class Person(val name: String?=null, var contacts : List<String?> = listOf(), var lastSeen: Date?=null, var emailAddress:String? = null)



data class Message(var from:String?=null, var value: String?=null, var to:String?=null, var sentTime: Date?=null)