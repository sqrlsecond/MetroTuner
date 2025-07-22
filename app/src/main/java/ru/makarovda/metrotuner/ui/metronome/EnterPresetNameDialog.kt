package ru.makarovda.metrotuner.ui.metronome


import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ru.makarovda.metrotuner.R
import java.lang.IllegalStateException

/**
 * Диалог для ввода имени пресета при его сохранении в БД
 */
class EnterPresetNameDialog: DialogFragment() {

    private var name: String? = null
    private var _listener: ResultListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater
            val dialogView = inflater.inflate(R.layout.dialog_profile_save,null)

            builder.setView(dialogView)
                .setPositiveButton("Save",
                    DialogInterface.OnClickListener {dialog, id->
                        name = dialogView.findViewById<EditText>(R.id.name_edit)?.text.toString()
                        name?.let{
                            _listener?.result(name!!)
                        }
                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                    //getDialog()?.cancel()
                })

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun setResultListener(listener: ResultListener){
        _listener = listener
    }

    interface ResultListener{
        fun result(name: String)
    }
}


