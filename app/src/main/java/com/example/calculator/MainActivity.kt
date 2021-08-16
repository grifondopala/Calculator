package com.example.calculator

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    private var exampleText : String = ""
    private var resultText : String = ""
    private var ExampleView: TextView ?= null
    private var ResultView: TextView ?= null

    private var gridView: GridView ? = null
    private var arrayList: ArrayList<LanguageItem>? = null
    private var languageAdapter: LanguageAdapter ? = null

    private var operationList = listOf<Char>('+','-','*','/')
    private var numberList = listOf<Char>('1','2','3','4','5','6','7','8','9','0','.')

    private var needChange: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridView = findViewById(R.id.buttons_grid_view)
        arrayList = setDataList()
        languageAdapter = LanguageAdapter(applicationContext, arrayList!!, this)
        gridView?.adapter = languageAdapter

        ExampleView = findViewById(R.id.example)
        ResultView = findViewById(R.id.result)
        ExampleView?.textSize = resources.getDimension(R.dimen.font_medium)
        ResultView?.textSize = resources.getDimension(R.dimen.font_small)
    }

    private fun setDataList(): ArrayList<LanguageItem>{
        var arrayList: ArrayList<LanguageItem> = ArrayList()

        arrayList.add(LanguageItem("1","Number"))
        arrayList.add(LanguageItem("2","Number"))
        arrayList.add(LanguageItem("3","Number"))
        arrayList.add(LanguageItem("C","Operation"))
        arrayList.add(LanguageItem("4","Number"))
        arrayList.add(LanguageItem("5","Number"))
        arrayList.add(LanguageItem("6","Number"))
        arrayList.add(LanguageItem("+","Operation"))
        arrayList.add(LanguageItem("7","Number"))
        arrayList.add(LanguageItem("8","Number"))
        arrayList.add(LanguageItem("9","Number"))
        arrayList.add(LanguageItem("-","Operation"))
        arrayList.add(LanguageItem("*","Operation"))
        arrayList.add(LanguageItem("0","Number"))
        arrayList.add(LanguageItem("/","Operation"))
        arrayList.add(LanguageItem("=","Operation"))
        arrayList.add(LanguageItem("(","Operation"))
        arrayList.add(LanguageItem(")","Operation"))
        arrayList.add(LanguageItem(".","Operation"))
        arrayList.add(LanguageItem("<-","Operation"))

        return arrayList
    }

    public fun onClickNumber(Name : String){
        if(!needChange){
            exampleText = ""
            ResultView?.text = ""
            changeTextSize()
        }
        exampleText+=Name
        ExampleView?.text = exampleText
        resultCount()
    }

    public fun onClickOperation(Name : String){
        if(!needChange && Name!="="){
            exampleText = resultText
            changeTextSize()
        }
        var lastSymbol = if(exampleText.isNotEmpty()) exampleText[exampleText.lastIndex] else ' '
        if(operationList.contains(Name[0])) {
            if(exampleText.isNotEmpty() && (lastSymbol!='(' || Name == "-")) exampleText = if(operationList.contains(lastSymbol) || lastSymbol == '.') exampleText.dropLast(1) + Name[0] else exampleText + Name[0]
        }
        else{
            when(Name){
                "<-" -> {
                    exampleText = exampleText.dropLast(1)
                    if(exampleText.isNotEmpty() && exampleText.elementAt(exampleText.lastIndex)!='(') resultCount()
                    else if(!exampleText.isNotEmpty()) ResultView?.text = ""
                }
                "C" -> clearExample()
                "("  -> addBracketRight(lastSymbol)
                ")" -> addBracketLeft(lastSymbol)
                "." -> addDot(lastSymbol)
                "=" -> if(needChange) changeTextSize()
            }
        }
        ExampleView?.text = exampleText
    }

    private fun clearExample(){
        exampleText = ""
        ResultView?.text = ""
        ExampleView?.textSize = resources.getDimension(R.dimen.font_medium)
        ResultView?.textSize = resources.getDimension(R.dimen.font_small)
        needChange = true
    }

    private fun addBracketRight(lastSymbol: Char){
        exampleText += if(exampleText.length == 0 || operationList.contains(lastSymbol) || lastSymbol == '(') "(" else "*("
    }

    private fun addBracketLeft(lastSymbol: Char){

        val countLeftBracket = exampleText.toCharArray().count{symbol -> symbol == ')'}
        val countRightBracket = exampleText.toCharArray().count{symbol -> symbol == '('}

        if(countRightBracket > countLeftBracket && exampleText.length > 1) {
            val last2Symbol = exampleText.get(exampleText.lastIndex-1).toString()+lastSymbol
            if(last2Symbol == "(-") exampleText+="1)"
            else if (operationList.contains(lastSymbol) || lastSymbol == '.') exampleText = exampleText.dropLast(1) + ")"
            else if (lastSymbol != '(') exampleText += ")"
        }

    }

    private fun addDot(lastSymbol: Char){
        var i = exampleText.lastIndex
        var count = 0
        while(i>=0 && numberList.contains(exampleText.elementAt(i))){
            if(exampleText.elementAt(i) == '.') count++
            i--
        }
        if(count == 0 && numberList.contains(lastSymbol)) exampleText+="."
    }

    private fun changeTextSize(){
        if(needChange) {
            val set: AnimatorSet = AnimatorInflater.loadAnimator(applicationContext, R.animator.decrease_text)
                .apply {
                    setTarget(ExampleView)
                    start()
                } as AnimatorSet
            val set1: AnimatorSet = AnimatorInflater.loadAnimator(applicationContext, R.animator.increase_text)
                .apply {
                    setTarget(ResultView)
                    start()
                } as AnimatorSet
        }else{
            ResultView?.textSize = resources.getDimension(R.dimen.font_small)
            ExampleView?.textSize = resources.getDimension(R.dimen.font_medium)
        }
        needChange = !needChange
    }

    private fun startCount() {
        addMissingBrackets()
        var countLeftBracket = exampleText.toCharArray().count{symbol -> symbol == ')'}
        while(countLeftBracket >= 0){
            var substringExample = if(countLeftBracket!=0) findSubstring() else exampleText
            var resultCount = сountSubstring(substringExample)
            exampleText = if(countLeftBracket != 0) exampleText.replaceFirst("($substringExample)", resultCount) else resultCount
            countLeftBracket--
        }
        if(exampleText.startsWith("&")) exampleText = "-" + exampleText.drop(1)

        var numberAfterDot = ""
        for(i in exampleText.lastIndex downTo 0){
            var charExample = exampleText.elementAt(i)
            if(charExample == '.') break
            numberAfterDot += charExample.toString()
        }
        if(numberAfterDot == "0") exampleText = exampleText.toDouble().roundToInt().toString()

    }

    private fun addMissingBrackets(){

        var countLeftBracket = exampleText.toCharArray().count{symbol -> symbol == ')'}
        var countRightBracket = exampleText.toCharArray().count{symbol -> symbol == '('}
        var lastSymbol = exampleText.elementAt(exampleText.lastIndex)
        var lastSymbol1 = if(exampleText.length > 1) exampleText.elementAt(exampleText.lastIndex-1).toString() else ""

        if((lastSymbol1+lastSymbol)=="(-"){
            exampleText+="1)"
            countLeftBracket++
        }else{
            while(operationList.contains(lastSymbol) || lastSymbol == '('){
                exampleText = exampleText.dropLast(1)
                lastSymbol = exampleText.elementAt(exampleText.lastIndex)
            }
        }
        while(countLeftBracket < countRightBracket){
            exampleText+=")"
            countLeftBracket++
        }

    }

    private fun findSubstring(): String {
        var indexRightBracket = 0
        var indexLeftBracket = 0
        var substring = ""

        for(i in 0..exampleText.lastIndex){
            if(exampleText.elementAt(i) == '(') indexRightBracket = i
            if(exampleText.elementAt(i) == ')'){
                indexLeftBracket = i
                break
            }
        }
        substring = exampleText.substring(indexRightBracket+1, indexLeftBracket)
        return substring
    }

    private fun сountSubstring(substring : String): String{

        var a: MutableList<String> = mutableListOf()
        var result = ""
        var elementExample: String = ""
        for(i in 0..substring.lastIndex){ // Массив с числами и операциями
            if(!operationList.contains(substring[i])) elementExample+=substring[i]
            else{
                if(elementExample.isNotEmpty()) a.add(elementExample)
                a.add(substring[i].toString())
                elementExample = ""
            }
        }
        if(elementExample.isNotEmpty()) a.add(elementExample)
        var i = 0
        while(i<a.size) {
            if (a.elementAt(i) == "*" || a.elementAt(i) == "/") a = operationCount(a.elementAt(i), i, a)
            else i++
        }
        i = 0
        while(i<a.size) {
            if (a.elementAt(i) == "+" || a.elementAt(i) == "-") a = operationCount(a.elementAt(i), i, a)
            else i++
        }
        result = a[0]
        return result
    }

    private fun operationCount(operation : String, i : Int, a: MutableList<String>) : MutableList<String>{
        var result = ""
        var number = 0.0
        if(operation == "-" && i==0){
            result = "&" + a.elementAt(1)
            a[1] = result
            a.removeAt(0)
        }else{
            var numberFirst = if(a[i-1].startsWith("&")) -1 * a[i-1].drop(1).toDouble() else a[i-1].toDouble()
            var numberSecond = if(a[i+1].startsWith("&")) -1 * a[i+1].drop(1).toDouble() else a[i+1].toDouble()
            when(operation){
                "*" -> number = numberFirst * numberSecond
                "/" -> number = numberFirst / numberSecond
                "-" -> number = numberFirst - numberSecond
                "+" -> number = numberFirst + numberSecond
            }
            result = if(number < 0) "&" + number.toString().drop(1) else number.toString()
            a[i+1] = result
            a.removeAt(i-1)
            a.removeAt(i-1)
        }
        return a
    }

    private fun resultCount(){
        resultText = exampleText
        if(resultText.isNotEmpty()){
            startCount()
            var a = exampleText
            exampleText = resultText
            resultText = a
            ResultView?.text = "=" + resultText
        }else{
            ResultView?.text = ""
        }
    }
}