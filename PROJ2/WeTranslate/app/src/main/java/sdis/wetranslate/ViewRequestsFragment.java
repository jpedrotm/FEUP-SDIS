package sdis.wetranslate;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import sdis.wetranslate.exceptions.ServerRequestException;
import sdis.wetranslate.logic.ServerRequest;
import sdis.wetranslate.logic.User;
import sdis.wetranslate.utils.GoodGuy;

import static sdis.wetranslate.utils.GoodGuy.changeFragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewRequestsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewRequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewRequestsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<Integer> requestsID=new ArrayList<>();
    private OnFragmentInteractionListener mListener;

    public ViewRequestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewRequestsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewRequestsFragment newInstance(String param1, String param2) {
        ViewRequestsFragment fragment = new ViewRequestsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ViewGroup viewRequests=(ViewGroup) inflater.inflate(R.layout.fragment_view_requests, container, false);

        feedUserRequests(viewRequests);

        return viewRequests;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void feedUserRequests(final ViewGroup viewRequests){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray requests= ServerRequest.getRequestsByUsername(User.getInstance().getUsername());
                    ArrayList<String> texts=new ArrayList<String>();
                    for(int i=0;i<requests.length();i++){
                        JSONObject object=requests.getJSONObject(i);
                        String textRequest=object.getString("content");
                        texts.add(textRequest);
                        requestsID.add(object.getInt("id"));
                    }

                    ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),R.layout.answers_request,texts);
                    ListView listView= (ListView) viewRequests.findViewById(R.id.listViewRequests);
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            User.getInstance().setCurrentRequestWatching(requestsID.get(position));
                            changeFragment(GoodGuy.FragmentType.VIEW_TRANSLATIONS,getActivity());
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ServerRequestException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
