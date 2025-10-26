import React from 'react'
import { Modal, List } from 'antd'

export default function TopScoresModal({ open, onClose, topScores = [], loading = false }) {
  return (
    <Modal
      title="Top Scores"
      open={open}
      onOk={onClose}
      onCancel={onClose}
      okText="Close"
      cancelButtonProps={{ style: { display: 'none' } }}
    >
      <List
        loading={loading}
        dataSource={topScores}
        renderItem={(item, idx) => (
          <List.Item key={idx}>
            <div style={{ display: 'flex', justifyContent: 'space-between', width: '100%' }}>
              <div>{item.word || '(empty)'}</div>
              <div>{item.score}</div>
            </div>
          </List.Item>
        )}
      />
    </Modal>
  )
}
