package proyectofinal.autocodes.view.siv;

import android.content.Context;
import android.util.AttributeSet;

import proyectofinal.autocodes.view.siv.shader.ShaderHelper;
import proyectofinal.autocodes.view.siv.shader.SvgShader;


public class ShapeImageView extends ShaderImageView {

    public ShapeImageView(Context context) {
        super(context);
    }

    public ShapeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShapeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ShaderHelper createImageViewHelper() {
        return new SvgShader();
    }
}
