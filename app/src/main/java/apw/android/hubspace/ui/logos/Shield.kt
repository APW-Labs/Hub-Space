package apw.android.hubspace.ui.logos

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val shield_lock: ImageVector
    get() {
        if (_shield_lock != null) {
            return _shield_lock!!
        }
        _shield_lock =
            ImageVector.Builder(
                name = "shield_lock",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            )
                .apply {
                    path(
                        fill = SolidColor(Color.Black),
                        fillAlpha = 1f,
                        stroke = null,
                        strokeAlpha = 1f,
                        strokeLineWidth = 1f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Bevel,
                        strokeLineMiter = 1f,
                        pathFillType = PathFillType.Companion.NonZero,
                    ) {
                        moveTo(12f, 22f)
                        quadTo(8.53f, 21.13f, 6.26f, 18.01f)
                        reflectiveQuadTo(4f, 11.1f)
                        verticalLineTo(5f)
                        lineTo(12f, 2f)
                        lineToRelative(8f, 3f)
                        verticalLineToRelative(6.1f)
                        quadToRelative(0f, 3.8f, -2.26f, 6.91f)
                        reflectiveQuadTo(12f, 22f)
                        close()
                        moveTo(10f, 16f)
                        horizontalLineToRelative(4f)
                        quadToRelative(0.43f, 0f, 0.71f, -0.29f)
                        reflectiveQuadTo(15f, 15f)
                        verticalLineTo(12f)
                        quadToRelative(0f, -0.43f, -0.29f, -0.71f)
                        reflectiveQuadTo(14f, 11f)
                        verticalLineTo(10f)
                        quadTo(14f, 9.17f, 13.41f, 8.59f)
                        reflectiveQuadTo(12f, 8f)
                        reflectiveQuadTo(10.59f, 8.59f)
                        reflectiveQuadTo(10f, 10f)
                        verticalLineToRelative(1f)
                        quadTo(9.58f, 11f, 9.29f, 11.29f)
                        reflectiveQuadTo(9f, 12f)
                        verticalLineToRelative(3f)
                        quadToRelative(0f, 0.42f, 0.29f, 0.71f)
                        quadTo(9.58f, 16f, 10f, 16f)
                        close()
                        moveToRelative(1f, -5f)
                        verticalLineTo(10f)
                        quadTo(11f, 9.57f, 11.29f, 9.29f)
                        reflectiveQuadTo(12f, 9f)
                        reflectiveQuadToRelative(0.71f, 0.29f)
                        reflectiveQuadTo(13f, 10f)
                        verticalLineToRelative(1f)
                        horizontalLineTo(11f)
                        close()
                    }
                }
                .build()
        return _shield_lock!!
    }

private var _shield_lock: ImageVector? = null