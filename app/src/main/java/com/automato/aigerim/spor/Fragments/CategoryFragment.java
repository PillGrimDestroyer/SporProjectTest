package com.automato.aigerim.spor.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.automato.aigerim.spor.Activity.MainActivity;
import com.automato.aigerim.spor.Adapter.CategoryAdapter;
import com.automato.aigerim.spor.Other.Tools.Tools;
import com.automato.aigerim.spor.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class CategoryFragment extends Fragment {

    private static View rootview;
    private static CategoryFragment instance;
    private static Tools tools = new Tools();
    private static CategoryAdapter adapter;

    private FirebaseDatabase database;
    private static DatabaseReference reference;
    private static HashMap<String, Integer> hashMapCategory;

    private static RecyclerView sporList;
    private static EditText searchField;
    private ImageView close;
    private static LinearLayoutManager llm;

    private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean order) {
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Сортируем в зависимости от значения
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                if (order) {
                    return o1.getValue().compareTo(o2.getValue());
                } else {
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });

        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            if (entry.getValue() != null)
                if (!entry.getValue().equals(0))
                    sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static CategoryFragment getInstance() {
        if (instance == null){
            instance = new CategoryFragment();
        }else {
            MainActivity.showLoader();
            adapter = null;
            fillData(false);
        }
        MainActivity.setCurentFragment(instance);
        return instance;
    }

    protected void finalize() throws Throwable {
        adapter = null;
        super.finalize();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_category, container, false);

        sporList = (RecyclerView) rootview.findViewById(R.id.spor_list);
        sporList.setHasFixedSize(true);

        llm = new LinearLayoutManager(this.getActivity());
        sporList.setLayoutManager(llm);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("spor");
        hashMapCategory = new HashMap<>();

        getActivity().findViewById(R.id.my_toolbar).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.shadow).setVisibility(View.VISIBLE);

        TextView title = (TextView) getActivity().findViewById(R.id.title);
        searchField = (EditText) getActivity().findViewById(R.id.search_field);
        close = (ImageView) getActivity().findViewById(R.id.close);

        title.setText("Категории");

        MainActivity.showLoader();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((EditText) MainActivity.getActivity().findViewById(R.id.search_field)).getText().clear();
                MainActivity.getActivity().findViewById(R.id.search_left).performClick();
                fillData(false);
            }
        });

        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                View focus = MainActivity.getActivity().getCurrentFocus();
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //прячу клаву и не только
                    if (focus != null) {
                        InputMethodManager imm = (InputMethodManager) MainActivity.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(focus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        if (searchField.getText().length() > 0) {
                            fillData(true);
                        }
                    }
                }
                return false;
            }
        });

        fillData(false);

        MainActivity.setCurentFragment(this);
        return rootview;
    }

    private static void fillData(final boolean isSorted) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String[] array = rootview.getResources().getStringArray(R.array.category);
                int length = array.length;
                hashMapCategory.clear();

                //заполняем всеми видами спорта
                for (int i = 0; i < length; i++) {
                    Integer catCounter = hashMapCategory.get(array[i]);
                    if (catCounter == null && !isSorted)
                        hashMapCategory.put(array[i], 0);
                }
                array = null;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot part : ds.getChildren()) {
                        if (part.getKey().equals("category")) {
                            String category = part.getValue().toString();

                            if (isSorted) {
                                if (tools.regex(searchField.getText().toString().toLowerCase(), category.toLowerCase())) {
                                    //считаем сколько споров в каждой категории
                                    Integer catCounter = hashMapCategory.get(category);
                                    hashMapCategory.put(category, catCounter == null ? 1 : catCounter + 1);
                                }
                            } else {
                                Integer catCounter = hashMapCategory.get(category);
                                hashMapCategory.put(category, catCounter == null ? 1 : catCounter + 1);
                            }

                        }

                    }
                }
                //сортировка
                Map<String, Integer> sortedMapAsc = sortByComparator(hashMapCategory, false);

                //ставим адаптер на RecyclerView
                adapter = new CategoryAdapter(sortedMapAsc, rootview.getContext(), MainActivity.getFragmetManeger(), rootview);
                sporList.setAdapter(adapter);

                sporList.setLayoutManager(llm);

                MainActivity.dismissWithAnimationLoader();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        clearAllData();
    }

    private void clearAllData() {
        rootview = null;
        instance = null;
        adapter = null;

        database = null;
        reference = null;
        hashMapCategory = null;

        sporList = null;
        searchField = null;
        close = null;
        llm = null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // write logic here b'z it is called when fragment is visible to user
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
