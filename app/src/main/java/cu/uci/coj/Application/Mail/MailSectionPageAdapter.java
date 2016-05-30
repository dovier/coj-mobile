package cu.uci.coj.Application.Mail;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by osvel on 2/22/16.
 */
public class MailSectionPageAdapter extends FragmentPagerAdapter {

    public MailSectionPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        System.out.println("lol "+position);

        switch (position){

            case 0: {
                //fragment inbox
                return MailListFragment.newInstance(MailFolder.INBOX);
            }
            case 1:{
                //fragment sent
//                return EditFragment.newInstance();
                return MailListFragment.newInstance(MailFolder.OUTBOX);
            }
            case 2:{
                //fragment draft
//                return EditFragment.newInstance();
                return MailListFragment.newInstance(MailFolder.DRAFT);
            }

        }

        return null;

    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Inbox";
            case 1:
                return "Sent";
            case 2:
                return "Drafts";
        }
        return null;
    }


}
