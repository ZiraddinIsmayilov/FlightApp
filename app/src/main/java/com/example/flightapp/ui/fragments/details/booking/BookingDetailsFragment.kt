package com.example.flightapp.ui.fragments.details.booking

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.flightapp.R
import com.example.flightapp.databinding.FragmentBookingDetailsBinding
import com.example.flightapp.model.Flight
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth

class BookingDetailsFragment : Fragment() {
    private lateinit var binding: FragmentBookingDetailsBinding
    private lateinit var mAuth: FirebaseAuth
    private var flight: Flight? = null


    companion object{
        var baggage: Int = 0
        var priceTotal : Long = 0L
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookingDetailsBinding.inflate(inflater)
        mAuth = FirebaseAuth.getInstance()
        setBaggageBottomSheet()
        setNavigation()
        setLayoutValue()

        return binding.root
    }


    private fun setNavigation() {
        binding.txtEdit.setOnClickListener {
            findNavController().navigate(R.id.action_bookingDetailsFragment_to_contactDetailsFragment)
        }
        binding.arrowBack2.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.imgGoPersonalInfo.setOnClickListener {
            findNavController().navigate(R.id.action_bookingDetailsFragment_to_passengerInfoFragment)
        }

        binding.btnSelect.setOnClickListener {
            val bundle = Bundle()
            flight?.price = flight?.price?.plus(baggage)
            priceTotal = (flight?.price ?: 0.0).toLong()

            bundle.putSerializable("flight", flight)
            findNavController().navigate(
                R.id.action_bookingDetailsFragment_to_selectSeatFragment,
                bundle
            )
        }


    }

    private fun setBaggageBottomSheet() {
        binding.btnAddBaggage.setOnClickListener {

            val dialog = BottomSheetDialog(requireContext())
            val view = layoutInflater.inflate(R.layout.bottom_sheet_baggage, null)
            dialog.setCancelable(true)
            val cardView1: CardView = view.findViewById(R.id.cardPBaggage1)
            val cardView2: CardView = view.findViewById(R.id.cardPBaggage2)
            val cardView3: CardView = view.findViewById(R.id.cardPBaggage3)
            val txtPrice: TextView = view.findViewById(R.id.txtPrice1)
            val txtPrice2: TextView = view.findViewById(R.id.txtPrice2)
            val btnAdd: Button = view.findViewById(R.id.btnAdd)


            val cardToTextMap = mapOf(
                cardView1 to listOf(R.id.txtBaggageWeight, R.id.txtBaggagePrice),
                cardView2 to listOf(R.id.txtBaggageWeight2, R.id.txtBaggagePrice2),
                cardView3 to listOf(R.id.txtBaggageWeight3, R.id.txtBaggagePrice3)
            )
            val colorWhite = ContextCompat.getColor(requireContext(), R.color.white)
            val colorBlue = ContextCompat.getColor(requireContext(), R.color.btn_blue)
            val colorGray = ContextCompat.getColor(requireContext(), R.color.txtGray)
            val colorDarkBlue = ContextCompat.getColor(requireContext(), R.color.txtDarkBlue)
            var count = 0

            cardToTextMap.forEach { (cardView, textViews) ->
                var checked = false
                cardView.setOnClickListener {

                    if (count == 0) {
                        cardView.setCardBackgroundColor(colorBlue)
                        checked = true
                        textViews.forEach { textViewId ->
                            val textView = cardView.findViewById<TextView>(textViewId)
                            textView?.setTextColor(colorWhite)
                            count = 1
                        }
                        when (cardView) {
                            cardView1 -> {
                                txtPrice.text = "$0"
                                txtPrice2.text = "$0"
                                baggage = 0
                            }

                            cardView2 -> {
                                txtPrice.text = "$28"
                                txtPrice2.text = "$28"
                                baggage = 5
                            }

                            cardView3 -> {
                                txtPrice.text = "$60"
                                txtPrice2.text = "$60"
                                baggage = 10
                            }
                        }
                    } else if (count == 1 && checked) {
                        cardView.setCardBackgroundColor(colorWhite)
                        count = 0
                        checked = false
                        txtPrice.text = "$0"
                        txtPrice2.text = "$0"
                        textViews.forEachIndexed { index, textViewId ->
                            val textView = cardView.findViewById<TextView>(textViewId)
                            if (index == 0 ) {
                                textView?.setTextColor(colorDarkBlue)
                            } else {
                                textView?.setTextColor(colorGray)
                            }
                        }

                    }

                    btnAdd.setOnClickListener {
                        binding.txtBaggage.text =
                            (baggage.toString() + "Kg") ?: getString(R.string.txtAddBaggage)
                        dialog.hide()
                    }


                }


                dialog.setContentView(view)
                dialog.show()


            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setLayoutValue() {
        binding.txtContactName.text = mAuth.currentUser?.displayName ?: "N/A"
        binding.txtContactEmail.text = mAuth.currentUser?.email ?: "N/A"
        binding.txtContactNumber.text = activity?.getSharedPreferences("UserDetails", Context.MODE_PRIVATE)?.getString("phone","No number assigned")
        binding.txtPassengerName.text = mAuth.currentUser?.displayName ?: "Add passenger details"
        arguments?.let {
            flight = it.getSerializable("flight", Flight::class.java)
            binding.txtSubtoal.text = flight?.price.toString()
        }
    }
}