package ru.makarovda.metrotuner.ui.metronome

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.makarovda.metrotuner.R

interface SetBpmDialogResultListener{
    fun onResult(bpm: Int)
}

class SetBpmDialog: DialogFragment(), TextView.OnEditorActionListener {

    //Обработчик данных с диалога
    var resultListener: SetBpmDialogResultListener? = null

    // Обработчик окна ввода текста
    private var editText: EditText? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { it ->
            // Объект для создания диалога
            val builder = AlertDialog.Builder(it)

            //Объект для создания view
            val inflater = requireActivity().layoutInflater

            //Создание View диалога
            val dialogView = inflater.inflate(R.layout.dialog_set_bpm, null)

            // Обработчик окна ввода текста
            editText = dialogView.findViewById<EditText>(R.id.set_bpm_dialog_edit_text)
            editText?.setOnEditorActionListener(this)
            editText?.requestFocus()

            builder.setView(dialogView)
                // Кнопки действия
                .setPositiveButton("Ok"){ _: DialogInterface, _: Int ->
                    transmitDataToListener()
                }
                .setNegativeButton("Cancel", null)
            builder.create().also {
                it.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            }
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    // Обработка ввода значения в EditText
    override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
        transmitDataToListener()
        dismiss()
        return true
    }

    private fun transmitDataToListener(){
        val str = editText?.text.toString()
        if (str.isNotEmpty()){
            val bpm = Integer.parseInt(str)
            resultListener?.onResult(bpm)
        }
    }
}