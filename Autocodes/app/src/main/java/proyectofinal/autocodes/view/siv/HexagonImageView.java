package proyectofinal.autocodes.view.siv;

import android.content.Context;
import android.util.AttributeSet;

import proyectofinal.autocodes.R;
import proyectofinal.autocodes.view.siv.shader.ShaderHelper;
import proyectofinal.autocodes.view.siv.shader.SvgShader;


public class HexagonImageView extends ShaderImageView {

    public HexagonImageView(Context context) {
        super(context);
    }

    public HexagonImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HexagonImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ShaderHelper createImageViewHelper() {
        return new SvgShader(R.raw.imgview_hexagon);
    }
}
