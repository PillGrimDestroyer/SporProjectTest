package spor.automato.com.sporprojecttest.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

import spor.automato.com.sporprojecttest.Activity.MainActivity;
import spor.automato.com.sporprojecttest.Adapter.CategoryAdapter;
import spor.automato.com.sporprojecttest.R;


public class CategoryFragment extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private HashMap<String, Integer> hashMapCategory;

    private RecyclerView sporList;

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
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootview = inflater.inflate(R.layout.fragment_category, container, false);

        this.sporList = (RecyclerView) rootview.findViewById(R.id.spor_list);
        sporList.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
        sporList.setLayoutManager(llm);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("spor");
        hashMapCategory = new HashMap<>();

        final FragmentManager manager = MainActivity.getFragmetManeger();

        TextView title = (TextView) getActivity().findViewById(R.id.title);
        title.setText("Категории");

        MainActivity.showLoader();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String[] array = rootview.getResources().getStringArray(R.array.category);
                int length = array.length;

                //заполняем всеми видами спорта
                for (int i = 0; i < length; i++) {
                    Integer catCounter = hashMapCategory.get(array[i]);
                    if (catCounter == null)
                        hashMapCategory.put(array[i], 0);
                }

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (DataSnapshot part : ds.getChildren()) {
                        if (part.getKey().equals("category")) {
                            String category = part.getValue().toString();

                            //считаем сколько споров в каждой категории
                            Integer catCounter = hashMapCategory.get(category);
                            hashMapCategory.put(category, catCounter == null ? 1 : catCounter + 1);

                        }

                    }
                }
                //сортировка
                Map<String, Integer> sortedMapAsc = sortByComparator(hashMapCategory, false);

                //ставим адаптер на RecyclerView
                CategoryAdapter adapter = new CategoryAdapter(sortedMapAsc, rootview.getContext(), manager, rootview);
                sporList.setAdapter(adapter);

                LinearLayoutManager layoutManager = new LinearLayoutManager(rootview.getContext());
                sporList.setLayoutManager(layoutManager);

                MainActivity.dismissWithAnimationLoader();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        reference.addListenerForSingleValueEvent(postListener);

        MainActivity.setCurentFragment(this);
        return rootview;
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
