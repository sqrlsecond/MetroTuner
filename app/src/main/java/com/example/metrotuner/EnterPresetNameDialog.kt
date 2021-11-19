package com.example.metrotuner


import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.lang.IllegalStateException

/**
 * Диалог для ввода имени пресета при его сохранении в БД
 */
class EnterPresetNameDialog(): DialogFragment() {

    var name: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.dialog_profile_save, null))
                .setPositiveButton("Save",
                    DialogInterface.OnClickListener {dialog, id->
                        name = view?.findViewById<EditText>(R.id.name_edit)?.text.toString()
                        //getDialog()?.cancel()
                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                    //getDialog()?.cancel()
                })

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
        }
    }
