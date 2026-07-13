export function DeviceSyncEffect({ active }: { active: boolean }) { return active ? <span className="sync-ripples" aria-hidden="true"><i /><i /><i /></span> : null }
