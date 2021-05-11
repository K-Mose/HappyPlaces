package com.example.happyplaces.util

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.happyplaces.R

// 콜백 만들고 RecyclerView 와 바인드 시켜야 됨
/*  ItemTouchHelper.SimpleCallback(드래그, 스왑) -> Flag가 0이면 ACTION_STATE_IDLE로 어떠한 이벤트 트리거를 가지지 못한 상태이다.
    getMovementFlag() 어떻게 움직일지에 대한 복합 플래그 반환
    onMove() 아이템 드래그 시 사용, 여기서는 사용하지 않기 때문에 false

    onChildDraw() 사용자 상호작용에서 커스터마이징 할 수 있는 부분.
                  dX : X 좌표 방향으로 끌어진 거리
                  dY : Y 좌표 방향으로 끌어진 거리
                  actionState : 1-swipe 8-drag
                  isCurrentlyActive : 사용자가 터치중일때만 true 이외에는 false


*/
abstract class SwipeToEditCallback(context: Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT){

    private val editIcon = ContextCompat.getDrawable(context, R.drawable.ic_edit_white_24dp)
    private val intrinsicWidth = editIcon!!.intrinsicWidth
    private val intrinsicHeight = editIcon!!.intrinsicHeight
    private val background = ColorDrawable()
    private val backgroundColor = Color.parseColor("#24AE05")
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        if(viewHolder.adapterPosition == 10) return 0
        return super.getMovementFlags(recyclerView, viewHolder)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onChildDraw(
            c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        Log.d("onChildDraw:" ,"$dX $dY $actionState $isCurrentlyActive")
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if(isCanceled){
            clearCanvas(c, itemView.left + dX, itemView.top.toFloat(), itemView.left.toFloat(), itemView.bottom.toFloat()) // ?
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        // Draw the green edit background
        background.color = backgroundColor
        background.setBounds(itemView.left + dX.toInt(), itemView.top, itemView.left, itemView.bottom)
        background.draw(c)

        // Calculate position of edit icon
        val editIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val editIconMargin = (itemHeight - intrinsicHeight)
        val editIconLeft = itemView.left + editIconMargin - intrinsicWidth
        val editIconRight = itemView.left + editIconMargin
        val editIconBottom = editIconTop + intrinsicHeight

        // Draw the edit icon
        editIcon!!.setBounds(editIconLeft, editIconTop, editIconRight, editIconBottom)
        editIcon.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float,  bottom: Float){
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}