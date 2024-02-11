package com.example.a7minuteworkoutapp

class ExerciseModel (
    private var id:Int,
    private var name:String,
    private var image:Int,
    private var isCompleted:Boolean =false,
    private var isSelected:Boolean = false){
    fun getId():Int{
        return  id
    }
    fun setId(id:Int){
        this.id = id
    }
    fun getName():String{
        return  name
    }
    fun setName(name:String){
        this.name = name
    }
    fun getImage():Int{
        return image
    }
    fun setImage(image:Int){
        this.image = image
    }
}