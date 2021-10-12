package com.bignerdbranch.android.criminalintent

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "${CRIMINAL_INTENT_TAG}_CrimeListFragment"
class CrimeListFragment : Fragment() {
    /**
     *Required Interface for hosting activities
     */
    interface Callbacks {
       fun onCrimeSelected(crimeId: UUID)
    }

    //DECLARING A CALLBACK VARIABLE
    private var callbacks: Callbacks? = null

    //DECLARE THE ADAPTER OBJECT AND RECYCLER VIEW OBJECT
    private var crimeRecyclerView: RecyclerView? = null
    private var adapter: CrimeAdapter? = null

    //CALL A REFERENCE TO THE CRIMELISTVIEWMODEL
    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    //THIS METHOD IS USED TO INSTANTIATE THE RECYCLERVIEW VIEW (IN THE XML)
    //AND ASSIGN THE ADAPTER TO THE RECYCLERVIEW
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView!!.layoutManager = LinearLayoutManager(this.context)
        crimeRecyclerView!!.adapter = adapter

        return view
    }

    /*
    CREATE THE OBSERVER AND ASSIGN TO THE LIVEDATA
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            { crimes ->
                crimes?.let {
                    Log.i(TAG, "Got crimes ${crimes.size}")
                    updateUI(crimes)
                }
            })

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks == null
    }

    /* Update the UI */
    private fun updateUI(crimes: List<Crime>) {
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView?.adapter = adapter
    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

    /**
     *DEFINE THE VIEW_HOLDER FOR THE RECYCLER VIEW
     */
    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        private lateinit var crime: Crime
        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            //format the date String
            val formatter = SimpleDateFormat("EEEE, MMMM dd YYYY.")
            val dateString = formatter.format(this.crime.date)

            dateTextView.text = dateString
            solvedImageView.visibility = if (crime.isSolved) View.VISIBLE else View.GONE
        }

        override fun onClick(v: View?) {
            callbacks?.onCrimeSelected(crime.id)
        }
    }

    /**
     * DEFINE THE ADAPTER CLASS FOR THE RECYCLERVIEW
     */
    private inner class CrimeAdapter(var crimes: List<Crime>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view)
        }

        override fun getItemCount(): Int {
            return crimes.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val crime: Crime = crimes[position]
            (holder as CrimeHolder).bind(crime)
        }

    }
}