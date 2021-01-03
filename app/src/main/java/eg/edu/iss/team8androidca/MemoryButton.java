package eg.edu.iss.team8androidca;

import android.content.Context;
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
        back = context.getDrawable(R.drawable.ic_question_mark);

        setBackground(back);

        GridLayout.LayoutParams tempParams = new GridLayout.LayoutParams(GridLayout.spec(r), GridLayout.spec(c));

        tempParams.width = (int) getResources().getDisplayMetrics().density * 180;
        tempParams.height = (int) getResources().getDisplayMetrics().density * 180;
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

