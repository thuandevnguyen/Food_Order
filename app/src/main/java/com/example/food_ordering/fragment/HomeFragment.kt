package com.example.food_ordering.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.food_ordering.R
import com.example.food_ordering.adapter.MenuAdapter
import com.example.food_ordering.adapter.VoucherAdapter
import com.example.food_ordering.databinding.FragmentHomeBinding
import com.example.food_ordering.model.AllItemMenu
import com.example.food_ordering.model.AllVoucher
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<AllItemMenu>
    private lateinit var voucherItems: MutableList<AllVoucher>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.txtViewMenu.setOnClickListener{
            val bottomSheetDialog = MenuBottomSheefFragment()
            bottomSheetDialog.show(parentFragmentManager, "Test")
        }

        retrieveMenu()
        retrieveVoucher()
        return binding.root
    }

    private fun retrieveVoucher() {
        database = FirebaseDatabase.getInstance()
        val voucherRef:DatabaseReference = database.reference.child("voucher")
        voucherItems = mutableListOf()
        voucherRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (voucherSnapshot in snapshot.children) {
                    val voucherItem = voucherSnapshot.getValue(AllVoucher::class.java)
                    voucherItem?.let { voucherItems.add(it) }
                }
                randomPopularItems()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun retrieveMenu() {
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("menu")
        menuItems = mutableListOf()
        //fetch data in firebase database
        foodRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val menuItem = foodSnapshot.getValue(AllItemMenu::class.java)
                    menuItem?.let { menuItems.add(it) }
                }
                setVoucherAdapter()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseError", "Error: ${error.message}")
            }
        })
    }

    private fun setVoucherAdapter() {
        val adapter = VoucherAdapter(voucherItems, requireContext())
        binding.voucherRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.voucherRecyclerView.adapter = adapter
    }

    private fun randomPopularItems() {
        val index = menuItems.indices.toList().shuffled()
        val numItemToShow = 6
        val subSetMenuItems = index.take(numItemToShow).map { menuItems[it] }

        setPopulerAdapter(subSetMenuItems)
    }

    private fun setPopulerAdapter(subsetmenuItems: List<AllItemMenu>) {
        val adapter = MenuAdapter(subsetmenuItems, requireContext())
        binding.PopularRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.PopularRecyclerView.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner1, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner2, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner3, ScaleTypes.FIT))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)
        imageSlider.setItemClickListener(object : ItemClickListener{
            override fun doubleClick(position: Int) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(position: Int) {
                val itemPosition = imageList[position]
                val itemMessage  = "Selected Image $position"
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
    companion object {

    }
}