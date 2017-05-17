package sdis.wetranslate;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import sdis.wetranslate.exceptions.ServerRequestException;
import sdis.wetranslate.logic.PagerFeeder;
import sdis.wetranslate.logic.ServerRequest;
import sdis.wetranslate.logic.User;

import static sdis.wetranslate.logic.ServerRequest.insertNewTranslation;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TranslateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TranslateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TranslateFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private boolean hasMadeRequest=false;//se já pediu um texto para traduzir
    private EditText textTranslated;
    private TranslationPagerAdapter adapter;
    private ViewPager viewPager;
    private Spinner dropdownFrom = null;
    private Spinner dropdownTo = null;
    private int dropdownFromPosition;
    private int dropdownToPosition;


    public TranslateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TranslateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TranslateFragment newInstance(String param1, String param2) {
        TranslateFragment fragment = new TranslateFragment();
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_screen_slide, container, false);

        viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        adapter = new TranslationPagerAdapter(getActivity());
        viewPager.setAdapter(adapter);

        return rootView;
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


    private class TranslationPagerAdapter extends PagerAdapter {
        private Context context;

        public TranslationPagerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = LayoutInflater.from(context);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.fragment_translate, container, false);
            container.addView(layout);



            TextView textView = (TextView) layout.findViewById(R.id.textToTranslate);

            // Decrease edit text's font size
            final EditText editText = (EditText) layout.findViewById(R.id.textTranslated);
            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.result_font));

            // Feed values to spinners
            String[] items = new String[]{"Portugues", "Ingles", "Alemao"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);


            dropdownFrom = (Spinner) layout.findViewById(R.id.spinnerTranslateFrom);
            dropdownFrom.setAdapter(adapter);
            dropdownFrom.setSelection(dropdownFromPosition);

            dropdownTo = (Spinner) layout.findViewById(R.id.spinnerTranslateTo);
            dropdownTo.setMinimumWidth(dropdownFrom.getWidth());
            dropdownTo.setAdapter(adapter);
            dropdownTo.setSelection(dropdownToPosition);

            textTranslated=(EditText) layout.findViewById(R.id.textTranslated);
            System.out.println("Text: "+textTranslated);


            // Listeners
            Button buttonRequests = (Button) layout.findViewById(R.id.buttonRequests);
            buttonRequests.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dropdownFromPosition = dropdownFrom.getSelectedItemPosition();
                    dropdownToPosition = dropdownTo.getSelectedItemPosition();
                    feedPager(dropdownFrom.getSelectedItem().toString(), dropdownTo.getSelectedItem().toString());
                    hasMadeRequest=true;
                }
            });

            Button sendTranslationButton = (Button) layout.findViewById(R.id.buttonTranslateSend);
            sendTranslationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendTraslation();
                }
            });


            JSONObject jsonObject = PagerFeeder.get(position);
            if (jsonObject != null) {
                try {
                    textView.setText(jsonObject.getString("content"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return layout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return PagerFeeder.count();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }


    private void feedPager(final String source, final String target) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray response = ServerRequest.getRequests(source, target);
                    PagerFeeder.feed(response);
                    viewPager.setAdapter(adapter);
                    viewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
                        private static final float ROT_MOD = -15f;

                        @Override
                        public void transformPage(View page, float position) {
                            final float width = page.getWidth();
                            final float height = page.getHeight();
                            final float rotation = ROT_MOD * position * -1.25f;

                            page.setPivotX(width * 0.5f);
                            page.setPivotY(height);
                            page.setRotation(rotation);
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

    private void sendTraslation(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String translatedText=textTranslated.getText().toString();
                System.out.println("Ola: "+hasMadeRequest+","+translatedText.length());

                if(hasMadeRequest){
                    if(translatedText.length()!=0){
                        try {
                            insertNewTranslation("manel1",translatedText,"1"); //para já ainda não pomos o id do request
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ServerRequestException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
