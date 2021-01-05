package eg.edu.iss.team8androidca;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.widget.GridLayout;

public class MemoryButton extends androidx.appcompat.widget.AppCompatButton {

    protected int row;
    protected int column;
    protected int frontDrawableId;

    protected boolean isFlipped = false;
    protected boolean isMatched = false;

    protected Drawable front;
    protected Drawable back;

    public MemoryButton(Context context, int r, int c, int frontImageDrawableId) {
        super(context);
        row = r;
        column = c;
        frontDrawableId = frontImageDrawableId;

        front = context.getDrawable(frontImageDrawableId);
        back = context.getDrawable(R.drawable.idea);

        setBackground(back);

        GridLayout.LayoutParams tempParams = new GridLayout.LayoutParams(GridLayout.spec(r), GridLayout.spec(c));

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            tempParams.width = Resources.getSystem().getDisplayMetrics().widthPixels / 3;
            tempParams.height = Resources.getSystem().getDisplayMetrics().heightPixels / 6;
        }
        else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            tempParams.width = Resources.getSystem().getDisplayMetrics().widthPixels / 7;
            tempParams.height = Resources.getSystem().getDisplayMetrics().heightPixels / 3;
        }
        tempParams.setMargins(1,1,1,1);
        setLayoutParams(tempParams);
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }

    public int getFrontDrawableId() {
        return frontDrawableId;
    }

    public void flip() {
        if (isMatched) {
            return;
        }
        if (isFlipped) {
            setBackground(back);
            isFlipped = false;
        } else {
            setBackground(front);
            isFlipped = true;
        }
    }
}

